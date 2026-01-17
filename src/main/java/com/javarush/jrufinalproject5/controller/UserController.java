package com.javarush.jrufinalproject5.controller;

import com.javarush.jrufinalproject5.dto.user.PatchUserIn;
import com.javarush.jrufinalproject5.dto.user.UserIn;
import com.javarush.jrufinalproject5.dto.user.UserOut;
import com.javarush.jrufinalproject5.service.UserService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/users")
@Validated
public class UserController {
    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping
    public List<UserOut> getAllUsers() {
        return userService.getAllUsers();
    }

    @GetMapping("/{id}")
    public UserOut getUserById(@PathVariable Long id) {
        return userService.findById(id);
    }

    @PostMapping
    public UserOut createUser(@Valid @RequestBody UserIn user) {
        return userService.createUser(user);
    }

    @PutMapping("/{id}")
    public UserOut updateUser(@PathVariable Long id, @Valid @RequestBody UserIn user) {
        return userService.updateUser(id, user);
    }

    @PatchMapping("/{id}")
    public UserOut patchUpdateUser(@PathVariable Long id, @Valid @RequestBody PatchUserIn user) {
        return userService.patchUpdateUser(id, user);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteUser(@PathVariable Long id) {
        userService.deleteUser(id);
        return ResponseEntity.noContent().build();
    }
}
