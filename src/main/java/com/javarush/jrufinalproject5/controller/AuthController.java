package com.javarush.jrufinalproject5.controller;

import com.javarush.jrufinalproject5.dto.user.UserLogIn;
import com.javarush.jrufinalproject5.dto.user.UserRegisterIn;
import com.javarush.jrufinalproject5.service.AuthService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import tools.jackson.databind.JsonNode;
import tools.jackson.databind.ObjectMapper;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/v1/auth")
@Validated
public class AuthController {
    private final AuthService authService;

    public AuthController(AuthService authService) {
        this.authService = authService;
    }

    @PostMapping("/monitoring")
    public ResponseEntity<Map<String, Object>> monitoring(@RequestParam String login, @RequestParam String password) {
        String token = authService.login(new UserLogIn(login, password));
        return ResponseEntity.ok(new HashMap<>() {{
            put("access_token", token);
            put("token_type", "Bearer");
            put("expires_in", 15000);
        }});
    }

    @PostMapping("/login")
    public ResponseEntity<Map<String, String>> login(@RequestBody UserLogIn user) {
        String token = authService.login(user);
        return ResponseEntity.ok(new HashMap<>() {{
            put("message", "User '%s' logged in!".formatted(user.getLogin()));
            put("token", token);
        }});
    }

    @PostMapping("/register")
    public ResponseEntity<Map<String, String>> register(@RequestBody @Valid UserRegisterIn user) {
        String token = authService.registerUser(user);
        return ResponseEntity.ok(new HashMap<>() {{
            put("message", "User '%s' has been successfully registered!".formatted(user.getLogin()));
            put("token", token);
        }});
    }
}