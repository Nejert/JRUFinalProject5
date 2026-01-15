package com.javarush.jrufinalproject5.service;

import com.javarush.jrufinalproject5.dto.TaskDto;
import com.javarush.jrufinalproject5.dto.task.TaskIn;
import com.javarush.jrufinalproject5.dto.task.TaskOut;
import com.javarush.jrufinalproject5.entity.Task;
import com.javarush.jrufinalproject5.repository.TaskRepository;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@AllArgsConstructor
public class TaskService {
    private final TaskRepository taskRepository;
    private final TaskDto mapper;

    public List<TaskOut> getAllTasks() {
        return taskRepository
                .findAll()
                .stream()
                .map(mapper::from)
                .collect(Collectors.toList());
    }

    public TaskOut findById(long id) {
        return taskRepository
                .findById(id)
                .map(mapper::from)
                .orElseThrow();
    }

    public TaskOut createTask(TaskIn task) {
        return mapper.from(taskRepository.save(mapper.from(task)));
    }

    public TaskOut updateTask(Long id, TaskIn task) {
        if (task.getId() == null || !id.equals(task.getId()))
            task.setId(id);
        return mapper.from(taskRepository.save(mapper.from(task)));
    }

    public TaskOut patchUpdateTask(Long id, TaskIn task) {
        Task dbTask = taskRepository.findById(id).orElseThrow(() -> new RuntimeException("Task not found!"));
        if (task.getTitle() != null) {
            dbTask.setTitle(task.getTitle());
        }
        if (task.getDescription() != null) {
            dbTask.setDescription(task.getDescription());
        }
        if (task.getDeadline() != null) {
            dbTask.setDeadline(task.getDeadline());
        }
        if (task.getStatus() != null) {
            dbTask.setStatus(task.getStatus());
        }
        return mapper.from(taskRepository.save(dbTask));
    }

    public void deleteUser(Long id) {
        taskRepository.findById(id).orElseThrow(() -> new RuntimeException("Task not found!"));
        taskRepository.deleteById(id);
    }
}
