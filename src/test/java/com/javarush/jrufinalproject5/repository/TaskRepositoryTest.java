package com.javarush.jrufinalproject5.repository;

import com.javarush.jrufinalproject5.entity.Task;
import com.javarush.jrufinalproject5.entity.User;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.data.jpa.test.autoconfigure.DataJpaTest;
import org.springframework.boot.jdbc.test.autoconfigure.AutoConfigureTestDatabase;
import org.springframework.boot.testcontainers.service.connection.ServiceConnection;
import org.springframework.test.annotation.Rollback;
import org.springframework.test.context.transaction.TestTransaction;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;

import static com.javarush.jrufinalproject5.entity.Role.USER;
import static org.junit.jupiter.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.assertThrows;

@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@Testcontainers
public class TaskRepositoryTest {
    @Container
    @ServiceConnection
    static PostgreSQLContainer<?> postgresContainer = new PostgreSQLContainer<>("postgres:latest");
    @Autowired
    private TaskRepository taskRepository;
    private final List<Task> initialTasksList = InitialDataBaseEntities.getInitialTasksList();

    @Test
    void findAllTest() {
        List<Task> all = taskRepository.findAll();
        assertEquals(initialTasksList, all);
    }

    @Test
    void findByIdTest() {
        Task task = taskRepository.findById(1L).orElseThrow();
        assertEquals(initialTasksList.get(0), task);
    }

    @Test
    void findByUserIdTest() {
        List<Task> tasks = taskRepository.findByUserId(1L);
        assertNotNull(tasks);
        List<Task> expected = List.of(InitialDataBaseEntities.getTask("homework"));
        assertEquals(expected, tasks);
    }

    @Test
    void saveTaskTest() {
        Task testTask = InitialDataBaseEntities.getTask("testTask");
        Task saved = taskRepository.save(testTask);
        Task dbTestTask = taskRepository.findById(saved.getId()).orElseThrow();
        assertNotNull(dbTestTask);
        assertEquals(testTask, dbTestTask);
        List<Task> list = taskRepository.findAll();
        assertEquals(initialTasksList.size() + 1, list.size());
    }
    @Test
    void updateTaskTest() {
        Task testTask = InitialDataBaseEntities.getTask("testTask");
        long savedId = taskRepository.save(testTask).getId();
        Task test = taskRepository.findById(savedId).orElseThrow();
        assertNotNull(test);
        String newStatus = "COMPLETED";
        test.setStatus(newStatus);
        taskRepository.save(test);

        Task task = taskRepository.findById(savedId).orElseThrow();
        assertEquals(newStatus, task.getStatus());
    }

    @Test
    void deleteTaskTest() {
        Task testTask = InitialDataBaseEntities.getTask("testTask");
        long savedId = taskRepository.save(testTask).getId();
        List<Task> all = taskRepository.findAll();
        assertNotEquals(initialTasksList, all);
        assertEquals(initialTasksList.size() + 1, all.size());
        taskRepository.deleteById(savedId);
        all = taskRepository.findAll();
        assertEquals(initialTasksList, all);
        assertThrows(NoSuchElementException.class, () ->
                taskRepository.findById(savedId).orElseThrow());
    }
}
