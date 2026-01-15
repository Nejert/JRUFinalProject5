package com.javarush.jrufinalproject5.service;

import com.javarush.jrufinalproject5.dto.TaskDto;
import com.javarush.jrufinalproject5.dto.task.TaskIn;
import com.javarush.jrufinalproject5.dto.task.TaskOut;
import com.javarush.jrufinalproject5.entity.Task;
import com.javarush.jrufinalproject5.repository.InitialDataBaseEntities;
import com.javarush.jrufinalproject5.repository.TaskRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mapstruct.factory.Mappers;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class TaskServiceTest {
    private static final TaskDto mapper = Mappers.getMapper(TaskDto.class);
    @Mock
    private TaskRepository taskRepository;
    private TaskService taskService;

    private Task first;
    private Task second;
    private Task third;
    private TaskOut firstOut;
    private TaskOut secondOut;
    private TaskOut thirdOut;
    private TaskIn thirdIn;

    @BeforeEach
    void setUp() {
        taskService = new TaskService(taskRepository, mapper);
        first = InitialDataBaseEntities.HOMEWORK;
        second = InitialDataBaseEntities.SERVER;
        firstOut = mapper.from(first);
        secondOut = mapper.from(second);
        thirdIn = new TaskIn(
                null,
                "newTask",
                "newTask",
                LocalDateTime.parse("2026-01-01T00:00:00"),
                "SOME_STATUS");
        third = mapper.from(thirdIn);
        thirdOut = mapper.from(third);
    }

    @Test
    void getAllTasksTest() {
        // Given
        when(taskRepository.findAll()).thenReturn(List.of(first, second));

        // When
        List<TaskOut> result = taskService.getAllTasks();

        // Then
        assertThat(result).hasSize(2);
        assertThat(result.get(0)).isEqualTo(firstOut);
        assertThat(result.get(1)).isEqualTo(secondOut);
        verify(taskRepository).findAll();
    }

    @Test
    void findByIdTest() {
        // Given
        when(taskRepository.findById(1L)).thenReturn(Optional.of(first));

        // When
        TaskOut result = taskService.findById(1L);

        // Then
        assertThat(result).isNotNull();
        assertThat(result).isEqualTo(firstOut);
    }

    @Test
    void findByIdExceptionTest() {
        // Given
        when(taskRepository.findById(1L)).thenReturn(Optional.empty());

        // When & Then
        assertThrows(NoSuchElementException.class, () -> taskService.findById(1L));
    }

    @Test
    void createTaskTest() {
        // Given
        when(taskRepository.save(third)).thenReturn(third);

        // When
        TaskOut result = taskService.createTask(thirdIn);

        // Then
        assertThat(result).isNotNull();
        assertThat(result).isEqualTo(thirdOut);
        verify(taskRepository).save(any(Task.class));
    }

    @Test
    void patchUpdateTaskTest() {
        // Given
        Long id = 1L;
        TaskIn patch = new TaskIn();
        patch.setTitle("newTitle");

        when(taskRepository.findById(id)).thenReturn(Optional.of(first));
        when(taskRepository.save(any(Task.class))).thenReturn(first);

        // When
        TaskOut result = taskService.patchUpdateTask(id, patch);

        // Then
        assertThat(result.getTitle()).isEqualTo("newTitle");
        verify(taskRepository).findById(id);
    }

    @Test
    void deleteUserTest() {
        // Given
        when(taskRepository.findById(1L)).thenReturn(Optional.of(first));

        // When
        taskService.deleteUser(1L);

        // Then
        verify(taskRepository).deleteById(1L);
    }

    @Test
    void deleteUserExceptionTest() {
        // Given
        when(taskRepository.findById(1L)).thenReturn(Optional.empty());

        // When & Then
        RuntimeException exception = assertThrows(RuntimeException.class,
                () -> taskService.deleteUser(1L));
        assertThat(exception.getMessage()).isEqualTo("Task not found!");
    }
}
