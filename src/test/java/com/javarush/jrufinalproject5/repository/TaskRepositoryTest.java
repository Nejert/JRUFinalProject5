package com.javarush.jrufinalproject5.repository;

import com.javarush.jrufinalproject5.config.Container;
import com.javarush.jrufinalproject5.entity.Task;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.data.jpa.test.autoconfigure.DataJpaTest;
import org.springframework.boot.test.context.SpringBootTest;

import java.time.LocalDateTime;
import java.util.List;
import java.util.NoSuchElementException;

import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
public class TaskRepositoryTest extends Container {
    @Autowired
    private TaskRepository taskRepository;

    @Test
    void findAllTest() {
        // When
        List<Task> all = taskRepository.findAll();
        // Then
        assertEquals(InitialDataBaseEntities.TASKS, all);
    }

    @Test
    void findByIdTest() {
        // When
        Task task = taskRepository.findById(1L).orElseThrow();
        // Then
        assertEquals(InitialDataBaseEntities.HOMEWORK, task);
    }

    @Test
    void findByUserIdTest() {
        // Given
        List<Task> expected = List.of(InitialDataBaseEntities.HOMEWORK);
        // When
        List<Task> tasks = taskRepository.findByUserId(1L);
        // Then
        assertNotNull(tasks);
        assertEquals(expected, tasks);
    }

    @Test
    void saveTaskTest() {
        // Given
        Task testTask = getTask("saveTaskTest");
        // When
        Task saved = taskRepository.save(testTask);
        Task dbTestTask = taskRepository.findById(saved.getId()).orElseThrow();
        // Then
        assertNotNull(dbTestTask);
        assertEquals(testTask, dbTestTask);
        List<Task> list = taskRepository.findAll();
        assertEquals(InitialDataBaseEntities.TASKS.size() + 1, list.size());
        deleteFromDB(saved);
    }

    @Test
    void updateTaskTest() {
        // Given
        Task testTask = getTask("updateTaskTest");
        String newStatus = "COMPLETED";
        // When
        Task saved = taskRepository.save(testTask);
        Task test = taskRepository.findById(saved.getId()).orElseThrow();
        // Then
        assertNotNull(test);
        test.setStatus(newStatus);
        taskRepository.save(test);

        Task task = taskRepository.findById(saved.getId()).orElseThrow();
        assertEquals(newStatus, task.getStatus());
        deleteFromDB(task);
    }

    @Test
    void deleteTaskTest() {
        // Given
        Task testTask = getTask("deleteTaskTest");
        // When
        Task saved = taskRepository.save(testTask);
        List<Task> all = taskRepository.findAll();
        // Then
        assertNotEquals(InitialDataBaseEntities.TASKS, all);
        assertEquals(InitialDataBaseEntities.TASKS.size() + 1, all.size());
        deleteFromDB(saved);
        all = taskRepository.findAll();
        assertEquals(InitialDataBaseEntities.TASKS, all);
    }

    private Task getTask(String title) {
        return new Task(
                null,
                title,
                title,
                LocalDateTime.parse("2026-01-01T00:00:00"),
                "SOME_STATUS", InitialDataBaseEntities.ADMIN);
    }

    private void deleteFromDB(Task task){
        taskRepository.deleteById(task.getId());
        assertThrows(NoSuchElementException.class, () ->
                taskRepository.findById(task.getId()).orElseThrow());
    }
}
