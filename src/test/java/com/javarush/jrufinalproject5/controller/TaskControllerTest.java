package com.javarush.jrufinalproject5.controller;

import com.javarush.jrufinalproject5.config.Container;
import com.javarush.jrufinalproject5.dto.TaskDto;
import com.javarush.jrufinalproject5.dto.task.TaskIn;
import com.javarush.jrufinalproject5.dto.task.TaskOut;
import com.javarush.jrufinalproject5.repository.InitialDataBaseEntities;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import tools.jackson.databind.ObjectMapper;

import java.time.LocalDateTime;
import java.util.List;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.hamcrest.Matchers.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
public class TaskControllerTest extends Container {
    private final static String URL = "/api/v1/tasks";
    @Autowired
    private MockMvc mockMvc;
    @Autowired
    private TaskDto mapper;
    @Autowired
    private ObjectMapper objectMapper;
    public static final String AUTHORIZATION = "Authorization";
    public String token;

    @BeforeEach
    void setUp() throws Exception {
        String resp = mockMvc.perform(post("/api/v1/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"login\": \"admin\", \"password\": \"admin\"}"))
                .andExpect(status().isOk())
                .andReturn().getResponse().getContentAsString();
        token = "Bearer " + objectMapper.readTree(resp).get("token").asString();
    }

    @Test
    void getAllTasksTest() throws Exception {
        // Given
        List<TaskOut> all = InitialDataBaseEntities.TASKS.stream()
                .map(mapper::from)
                .toList();
        String expected = objectMapper.writeValueAsString(all);
        // When & Then
        String responseJson = mockMvc.perform(get(URL).header(AUTHORIZATION, token))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.*", hasSize(all.size())))
                .andReturn().getResponse().getContentAsString();
        assertThat(responseJson).isEqualTo(expected);
    }

    @Test
    void getTaskByIdTest() throws Exception {
        // When & Then
        mockMvc.perform(get(URL + "/1").header(AUTHORIZATION, token))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.title", is(InitialDataBaseEntities.HOMEWORK.getTitle())))
                .andExpect(jsonPath("$.description", is(InitialDataBaseEntities.HOMEWORK.getDescription())));
    }

    @Test
    void notFoundTaskByIdTest() throws Exception {
        // When & Then
        mockMvc.perform(get(URL + "/-1").header(AUTHORIZATION, token))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.code", is(HttpStatus.NOT_FOUND.value())))
                .andExpect(jsonPath("$.messages[0]", is("No value present")));
    }

    @Test
    void createTaskTest() throws Exception {
        // Given
        TaskIn task = getTaskIn("createTaskTest");
        // When
        String responseJson = mockMvc.perform(post(URL)
                        .header(AUTHORIZATION, token)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(task)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", notNullValue()))
                .andExpect(jsonPath("$.title", is(task.getTitle())))
                .andReturn().getResponse().getContentAsString();
        // Then
        Long createdId = objectMapper.readValue(responseJson, TaskOut.class).getId();
        mockMvc.perform(get(URL + "/" + createdId).header(AUTHORIZATION, token))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(createdId))
                .andExpect(jsonPath("$.title", is(task.getTitle())));
        deleteFromDB(createdId);
    }

    @Test
    void createNotValidTaskTest() throws Exception {
        // Given
        List<TaskOut> all = InitialDataBaseEntities.TASKS.stream()
                .map(mapper::from)
                .toList();
        TaskIn task = new TaskIn(null, "x", null, null, null, null);
        // When & Then
        mockMvc.perform(post(URL)
                        .header(AUTHORIZATION, token)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(task)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.code", is(HttpStatus.BAD_REQUEST.value())))
                .andExpect(jsonPath("$.messages", containsInAnyOrder(startsWith("title"), startsWith("description"))));
        mockMvc.perform(get(URL).header(AUTHORIZATION, token))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.*", hasSize(all.size())));
    }

    @Test
    void updateTaskTest() throws Exception {
        // Given
        TaskIn task = getTaskIn("updateTaskTest");
        String newTitle = "NewTitle";
        TaskOut taskOut = createTask(task);
        Long id = taskOut.getId();
        // When
        task.setTitle(newTitle);
        mockMvc.perform(put(URL + "/" + id)
                        .header(AUTHORIZATION, token)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(task)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(id))
                .andExpect(jsonPath("$.title", is(task.getTitle())));
        // Then
        mockMvc.perform(get(URL + "/" + id).header(AUTHORIZATION, token))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(id))
                .andExpect(jsonPath("$.title", is(task.getTitle())));
        deleteFromDB(id);
    }

    @Test
    void updateNotValidTaskTest() throws Exception {
        // Given
        String oldTitle = "updateNotValidTaskTest";
        String newTitle = "X";
        TaskIn task = getTaskIn(oldTitle);
        TaskOut taskOut = createTask(task);
        Long id = taskOut.getId();
        // When
        task.setTitle(newTitle);
        mockMvc.perform(put(URL + "/" + id)
                        .header(AUTHORIZATION, token)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(task)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.code", is(is(HttpStatus.BAD_REQUEST.value()))))
                .andExpect(jsonPath("$.messages", contains(startsWith("title"))));
        // Then
        mockMvc.perform(get(URL + "/" + id).header(AUTHORIZATION, token))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(id))
                .andExpect(jsonPath("$.title", is(oldTitle)));
        deleteFromDB(id);
    }

    @Test
    void notFoundTaskForUpdate() throws Exception {
        TaskIn task = getTaskIn("notFoundTaskForUpdate");
        // When & Then
        mockMvc.perform(put(URL + "/-1")
                        .header(AUTHORIZATION, token)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(task)))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.code", is(HttpStatus.NOT_FOUND.value())))
                .andExpect(jsonPath("$.messages[0]", is("Task not found!")));
    }

    @Test
    void patchUpdateTaskTest() throws Exception {
        // Given
        String oldTitle = "patchUpdateTaskTest";
        String newTitle = "newTitle";
        String jsonWithNewTitle = "{\"title\": \"" + newTitle + "\"}";
        TaskIn task = getTaskIn(oldTitle);
        TaskOut taskOut = createTask(task);
        Long id = taskOut.getId();
        // When
        mockMvc.perform(patch(URL + "/" + id)
                        .header(AUTHORIZATION, token)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(jsonWithNewTitle))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(id))
                .andExpect(jsonPath("$.title", is(newTitle)));
        // Then
        mockMvc.perform(get(URL + "/" + id).header(AUTHORIZATION, token))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(id))
                .andExpect(jsonPath("$.title", is(newTitle)));
        deleteFromDB(id);
    }

    @Test
    void patchUpdateNotValidTaskTest() throws Exception {
        // Given
        String oldTitle = "patchUpdateNotValidTaskTest";
        String newTitle = "X";
        String jsonWithNewTitle = "{\"title\": \"" + newTitle + "\"}";
        TaskIn task = getTaskIn(oldTitle);
        TaskOut taskOut = createTask(task);
        Long id = taskOut.getId();
        // When
        mockMvc.perform(patch(URL + "/" + id)
                        .header(AUTHORIZATION, token)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(jsonWithNewTitle))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.code", is(is(HttpStatus.BAD_REQUEST.value()))))
                .andExpect(jsonPath("$.messages", contains(startsWith("title"))));
        // Then
        mockMvc.perform(get(URL + "/" + id).header(AUTHORIZATION, token))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(id))
                .andExpect(jsonPath("$.title", is(oldTitle)));
        deleteFromDB(id);
    }

    @Test
    void notFoundTaskForPatchUpdate() throws Exception {
        // When & Then
        mockMvc.perform(patch(URL + "/-1")
                        .header(AUTHORIZATION, token)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{}"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.code", is(HttpStatus.NOT_FOUND.value())))
                .andExpect(jsonPath("$.messages[0]", is("Task not found!")));
    }

    @Test
    void deleteTaskTest() throws Exception {
        // Given
        TaskIn task = getTaskIn("deleteTaskTest");
        TaskOut taskOut = createTask(task);
        // When & Then
        deleteFromDB(taskOut.getId());
    }

    @Test
    void notFoundTaskForDeleteTest() throws Exception {
        // When & Then
        mockMvc.perform(delete(URL + "/-1").header(AUTHORIZATION, token))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.code", is(HttpStatus.NOT_FOUND.value())))
                .andExpect(jsonPath("$.messages[0]", is("Task not found!")));
    }

    private TaskIn getTaskIn(String title) {
        return new TaskIn(
                null,
                title,
                title,
                LocalDateTime.parse("2026-01-01T00:00:00"),
                "SOME_STATUS", 1L);
    }

    private TaskOut createTask(TaskIn taskInIn) throws Exception {
        String responseJson = mockMvc.perform(post(URL)
                        .header(AUTHORIZATION, token)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(taskInIn)))
                .andExpect(status().isOk())
                .andReturn().getResponse().getContentAsString();
        return objectMapper.readValue(responseJson, TaskOut.class);
    }

    private void deleteFromDB(Long id) throws Exception {
        mockMvc.perform(delete(URL + "/" + id).header(AUTHORIZATION, token))
                .andExpect(status().isNoContent());
        mockMvc.perform(get(URL + "/" + id).header(AUTHORIZATION, token))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.code", is(HttpStatus.NOT_FOUND.value())))
                .andExpect(jsonPath("$.messages[0]", is("No value present")));
    }
}
