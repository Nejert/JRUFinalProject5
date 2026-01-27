package com.javarush.jrufinalproject5.service;

import com.javarush.jrufinalproject5.dto.TaskDto;
import com.javarush.jrufinalproject5.dto.task.PatchTaskIn;
import com.javarush.jrufinalproject5.dto.task.TaskIn;
import com.javarush.jrufinalproject5.dto.task.TaskOut;
import com.javarush.jrufinalproject5.entity.Task;
import com.javarush.jrufinalproject5.repository.InitialDataBaseEntities;
import com.javarush.jrufinalproject5.repository.TaskRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
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
    @Mock
    private TaskRepository taskRepository;
    @Mock
    private TaskDto mapper;
    @InjectMocks
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

        first = InitialDataBaseEntities.getClone(InitialDataBaseEntities.HOMEWORK);
        second = InitialDataBaseEntities.getClone(InitialDataBaseEntities.SERVER);
        firstOut = new TaskOut(first.getId(), first.getTitle(), first.getDescription(), first.getDeadline(), first.getStatus(), first.getUser().getId());
        secondOut = new TaskOut(second.getId(), second.getTitle(), second.getDescription(), second.getDeadline(), second.getStatus(), second.getUser().getId());
        thirdIn = new TaskIn(
                null,
                "newTask",
                "newTask",
                LocalDateTime.parse("2026-01-01T00:00:00"),
                "SOME_STATUS", 1L);
        third = new Task(thirdIn.getId(), thirdIn.getTitle(), thirdIn.getDescription(), thirdIn.getDeadline(), thirdIn.getStatus(), InitialDataBaseEntities.ADMIN);
        thirdOut = new TaskOut(third.getId(), third.getTitle(), third.getDescription(), third.getDeadline(), third.getStatus(), third.getUser().getId());
    }

    @Test
    void getAllTasksTest() {
        // Given
        when(taskRepository.findAll()).thenReturn(List.of(first, second));
        when(mapper.from(first)).thenReturn(firstOut);
        when(mapper.from(second)).thenReturn(secondOut);
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
        when(mapper.from(first)).thenReturn(firstOut);
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
        when(mapper.from(thirdIn)).thenReturn(third);
        when(mapper.from(third)).thenReturn(thirdOut);
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
        PatchTaskIn patch = new PatchTaskIn();
        patch.setTitle("newTitle");
        patch.setDescription("newDescription");
        patch.setStatus("newStatus");
        TaskOut expected = new TaskOut(
                firstOut.getId(),
                patch.getTitle(),
                patch.getDescription(),
                first.getDeadline(),
                patch.getStatus(),
                firstOut.getUserId()
                );
        when(taskRepository.findById(id)).thenReturn(Optional.of(first));
        when(taskRepository.save(any(Task.class))).thenReturn(first);
        when(mapper.from(any(Task.class))).thenReturn(expected);
        // When
        TaskOut result = taskService.patchUpdateTask(id, patch);
        // Then
        assertThat(result.getTitle()).isEqualTo(patch.getTitle());
        assertThat(result.getDescription()).isEqualTo(patch.getDescription());
        assertThat(result.getStatus()).isEqualTo(patch.getStatus());
        verify(taskRepository).findById(id);
    }

    @Test
    void deleteTaskTest() {
        // Given
        when(taskRepository.findById(1L)).thenReturn(Optional.of(first));
        // When
        taskService.deleteTask(1L);
        // Then
        verify(taskRepository).deleteById(1L);
    }

    @Test
    void deleteTaskExceptionTest() {
        // Given
        when(taskRepository.findById(1L)).thenReturn(Optional.empty());
        // When & Then
        RuntimeException exception = assertThrows(NoSuchElementException.class,
                () -> taskService.deleteTask(1L));
        assertThat(exception.getMessage()).isEqualTo("Task not found!");
    }
}
