package com.javarush.jrufinalproject5.controller;

import com.javarush.jrufinalproject5.config.Container;
import com.javarush.jrufinalproject5.dto.UserDto;
import com.javarush.jrufinalproject5.dto.user.UserIn;
import com.javarush.jrufinalproject5.dto.user.UserOut;
import com.javarush.jrufinalproject5.entity.Role;
import com.javarush.jrufinalproject5.repository.InitialDataBaseEntities;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import tools.jackson.databind.ObjectMapper;

import java.util.List;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.hamcrest.Matchers.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
public class UserControllerTest extends Container {
    private final static String URL = "/api/v1/users";
    @Autowired
    private MockMvc mockMvc;
    @Autowired
    private UserDto mapper;
    @Autowired
    private ObjectMapper objectMapper;

    @Test
    void getAllUsersTest() throws Exception {
        // Given
        List<UserOut> all = InitialDataBaseEntities.USERS.stream()
                .map(mapper::from)
                .toList();
        String expected = objectMapper.writeValueAsString(all);
        // When & Then
        String responseJson = mockMvc.perform(get(URL))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.*", hasSize(all.size())))
                .andReturn().getResponse().getContentAsString();
        assertThat(responseJson).isEqualTo(expected);
    }

    @Test
    void getUserByIdTest() throws Exception {
        // When & Then
        mockMvc.perform(get(URL + "/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.login", is(InitialDataBaseEntities.ADMIN.getLogin())))
                .andExpect(jsonPath("$.email", is(InitialDataBaseEntities.ADMIN.getEmail())));
    }

    @Test
    void notFoundUserByIdTest() throws Exception {
        // When & Then
        mockMvc.perform(get(URL + "/-1"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.code", is(HttpStatus.NOT_FOUND.value())))
                .andExpect(jsonPath("$.messages[0]", is("No value present")));
    }

    @Test
    void createUserTest() throws Exception {
        // Given
        UserIn user = getUserIn("createUserTest");
        // When
        String responseJson = mockMvc.perform(post(URL)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(user)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", notNullValue()))
                .andExpect(jsonPath("$.login", is(user.getLogin())))
                .andReturn().getResponse().getContentAsString();
        // Then
        Long createdId = objectMapper.readValue(responseJson, UserOut.class).getId();
        mockMvc.perform(get(URL + "/" + createdId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(createdId))
                .andExpect(jsonPath("$.login", is(user.getLogin())));
        deleteFromDB(createdId);
    }

    @Test
    void createNotValidUserTest() throws Exception {
        // Given
        List<UserOut> all = InitialDataBaseEntities.USERS.stream()
                .map(mapper::from)
                .toList();
        UserIn user = new UserIn(null, "x", "y", "z", null);
        // When & Then
        mockMvc.perform(post(URL)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(user)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.code", is(HttpStatus.BAD_REQUEST.value())))
                .andExpect(jsonPath("$.messages", containsInAnyOrder(startsWith("login"), startsWith("password"), startsWith("role"), startsWith("email"))));
        mockMvc.perform(get(URL))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.*", hasSize(all.size())));
    }

    @Test
    void updateUserTest() throws Exception {
        // Given
        UserIn user = getUserIn("updateUserTest");
        String newLogin = "NewLogin";
        UserOut userOut = createUser(user);
        Long id = userOut.getId();
        // When
        user.setLogin(newLogin);
        mockMvc.perform(put(URL + "/" + id)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(user)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(id))
                .andExpect(jsonPath("$.login", is(user.getLogin())));
        // Then
        mockMvc.perform(get(URL + "/" + id))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(id))
                .andExpect(jsonPath("$.login", is(user.getLogin())));
        deleteFromDB(id);
    }

    @Test
    void updateNotValidUserTest() throws Exception {
        // Given
        String oldLogin = "updateNotValidUserTest";
        String newLogin = "X";
        UserIn user = getUserIn(oldLogin);
        UserOut userOut = createUser(user);
        Long id = userOut.getId();
        // When
        user.setLogin(newLogin);
        mockMvc.perform(put(URL + "/" + id)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(user)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.code", is(is(HttpStatus.BAD_REQUEST.value()))))
                .andExpect(jsonPath("$.messages", contains(startsWith("login"))));
        // Then
        mockMvc.perform(get(URL + "/" + id))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(id))
                .andExpect(jsonPath("$.login", is(oldLogin)));
        deleteFromDB(id);
    }

    @Test
    void notFoundUserForUpdate() throws Exception {
        // Given
        UserIn user = getUserIn("notFoundUserForUpdate");
        // When & Then
        mockMvc.perform(put(URL + "/-1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(user)))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.code", is(HttpStatus.NOT_FOUND.value())))
                .andExpect(jsonPath("$.messages[0]", is("User not found!")));
    }

    @Test
    void patchUpdateUserTest() throws Exception {
        // Given
        String oldLogin = "patchUpdateUserTest";
        String newLogin = "newLogin";
        String jsonWithNewLogin = "{\"login\": \"" + newLogin + "\"}";
        UserIn user = getUserIn(oldLogin);
        UserOut userOut = createUser(user);
        Long id = userOut.getId();
        // When
        mockMvc.perform(patch(URL + "/" + id)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(jsonWithNewLogin))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(id))
                .andExpect(jsonPath("$.login", is(newLogin)));
        // Then
        mockMvc.perform(get(URL + "/" + id))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(id))
                .andExpect(jsonPath("$.login", is(newLogin)));
        deleteFromDB(id);
    }

    @Test
    void patchUpdateNotValidUserTest() throws Exception {
        // Given
        String oldLogin = "patchUpdateNotValidUserTest";
        String newLogin = "X";
        String jsonWithNewLogin = "{\"login\": \"" + newLogin + "\"}";
        UserIn user = getUserIn(oldLogin);
        UserOut userOut = createUser(user);
        Long id = userOut.getId();
        // When
        mockMvc.perform(patch(URL + "/" + id)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(jsonWithNewLogin))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.code", is(is(HttpStatus.BAD_REQUEST.value()))))
                .andExpect(jsonPath("$.messages", contains(startsWith("login"))));
        // Then
        mockMvc.perform(get(URL + "/" + id))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(id))
                .andExpect(jsonPath("$.login", is(oldLogin)));
        deleteFromDB(id);
    }

    @Test
    void notFoundUserForPatchUpdate() throws Exception {
        // When & Then
        mockMvc.perform(patch(URL + "/-1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{}"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.code", is(HttpStatus.NOT_FOUND.value())))
                .andExpect(jsonPath("$.messages[0]", is("User not found!")));
    }

    @Test
    void deleteUserTest() throws Exception {
        // Given
        UserIn user = getUserIn("deleteUserTest");
        UserOut userOut = createUser(user);
        // When & Then
        deleteFromDB(userOut.getId());
    }

    @Test
    void notFoundUserForDeleteTest() throws Exception {
        // When & Then
        mockMvc.perform(delete(URL + "/-1"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.code", is(HttpStatus.NOT_FOUND.value())))
                .andExpect(jsonPath("$.messages[0]", is("User not found!")));
    }

    private UserOut createUser(UserIn userIn) throws Exception {
        String responseJson = mockMvc.perform(post(URL)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(userIn)))
                .andExpect(status().isOk())
                .andReturn().getResponse().getContentAsString();
        return objectMapper.readValue(responseJson, UserOut.class);
    }

    private UserIn getUserIn(String login) {
        return new UserIn(
                null,
                login,
                login,
                "test@user.com",
                Role.ADMIN);
    }

    private void deleteFromDB(Long id) {
        mockMvc.perform(delete(URL + "/" + id))
                .andExpect(status().isNoContent());
        mockMvc.perform(get(URL + "/" + id))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.code", is(HttpStatus.NOT_FOUND.value())))
                .andExpect(jsonPath("$.messages[0]", is("No value present")));
    }
}
