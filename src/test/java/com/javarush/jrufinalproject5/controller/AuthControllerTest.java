package com.javarush.jrufinalproject5.controller;

import com.javarush.jrufinalproject5.config.Container;
import com.javarush.jrufinalproject5.dto.user.UserRegisterIn;
import com.javarush.jrufinalproject5.security.JwtUtils;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import tools.jackson.databind.JsonNode;
import tools.jackson.databind.ObjectMapper;

import static org.hamcrest.Matchers.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
public class AuthControllerTest extends Container {
    private final static String URL = "/api/v1/auth";
    @Autowired
    private MockMvc mockMvc;
    @Autowired
    private ObjectMapper objectMapper;

    @Test
    public void monitoringTest() throws Exception {
        // When & Then
        mockMvc.perform(post(URL + "/monitoring")
                        .param("login", "admin")
                        .param("password", "admin"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.access_token", notNullValue()))
                .andExpect(jsonPath("$.token_type").value("Bearer"))
                .andExpect(jsonPath("$.expires_in").value(JwtUtils.EXPIRATION));
    }

    @Test
    public void registerTest() throws Exception {
        // When & Then
        UserRegisterIn testUser = new UserRegisterIn("TestUser", "TestUser", "test@user.com");
        String userToken = mockMvc.perform(post(URL + "/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(testUser)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message", containsString(testUser.getLogin())))
                .andExpect(jsonPath("$.user_id", notNullValue()))
                .andExpect(jsonPath("$.token", notNullValue()))
                .andReturn().getResponse().getContentAsString();

        JsonNode jsonNodeUser = objectMapper.readTree(userToken);
        long userId = jsonNodeUser.get("user_id").asLong();

        String resp = mockMvc.perform(post("/api/v1/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"login\": \"admin\", \"password\": \"admin\"}"))
                .andExpect(status().isOk())
                .andReturn().getResponse().getContentAsString();
        String token = "Bearer " + objectMapper.readTree(resp).get("token").asString();

        mockMvc.perform(delete(UserControllerTest.URL + "/" + userId).header(UserControllerTest.AUTHORIZATION, token))
                .andExpect(status().isNoContent());
        mockMvc.perform(get(UserControllerTest.URL + "/" + userId).header(UserControllerTest.AUTHORIZATION, token))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.code", is(HttpStatus.NOT_FOUND.value())))
                .andExpect(jsonPath("$.messages[0]", is("No value present")));
    }

    @Test
    public void handleBadCredentialsExceptionTest() throws Exception {
        // When & Then
        mockMvc.perform(post(URL + "/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"login\": \"!admin!\", \"password\": \"!admin!\"}"))
                .andExpect(status().isForbidden())
                .andExpect(jsonPath("$.code", is(HttpStatus.FORBIDDEN.value())))
                .andExpect(jsonPath("$.messages", hasSize(greaterThanOrEqualTo(1))));
    }

    @Test
    public void handleEntityExistsExceptionTest() throws Exception {
        // When & Then
        mockMvc.perform(post(URL + "/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"login\": \"admin\", \"password\": \"admin\"}"))
                .andExpect(status().isForbidden())
                .andExpect(jsonPath("$.code", is(HttpStatus.FORBIDDEN.value())))
                .andExpect(jsonPath("$.messages", hasSize(greaterThanOrEqualTo(1))));
    }
}
