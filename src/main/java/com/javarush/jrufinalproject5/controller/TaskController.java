package com.javarush.jrufinalproject5.controller;

import com.javarush.jrufinalproject5.dto.task.PatchTaskIn;
import com.javarush.jrufinalproject5.dto.task.TaskIn;
import com.javarush.jrufinalproject5.dto.task.TaskOut;
import com.javarush.jrufinalproject5.dto.user.PatchUserIn;
import com.javarush.jrufinalproject5.dto.user.UserOut;
import com.javarush.jrufinalproject5.service.TaskService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/tasks")
@Validated
public class TaskController {
    private final TaskService taskService;

    public TaskController(TaskService taskService) {
        this.taskService = taskService;
    }

    @GetMapping
    public List<TaskOut> getAllTasks() {
        return taskService.getAllTasks();
    }

    @GetMapping("/{id}")
    public TaskOut getTaskById(@PathVariable Long id) {
        return taskService.findById(id);
    }

    @PostMapping
    public TaskOut createTask(@Valid @RequestBody TaskIn task) {
        return taskService.createTask(task);
    }

    @PutMapping("/{id}")
    public TaskOut updateTask(@PathVariable Long id, @Valid @RequestBody TaskIn task) {
        return taskService.updateTask(id, task);
    }

    @PatchMapping("/{id}")
    public TaskOut patchUpdateUser(@PathVariable Long id, @Valid @RequestBody PatchTaskIn task) {
        return taskService.patchUpdateTask(id, task);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteTask(@PathVariable Long id) {
        taskService.deleteTask(id);
        return ResponseEntity.noContent().build();
    }
}
