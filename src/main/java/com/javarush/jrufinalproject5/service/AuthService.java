package com.javarush.jrufinalproject5.service;

import com.javarush.jrufinalproject5.dto.UserDto;
import com.javarush.jrufinalproject5.dto.user.UserLogIn;
import com.javarush.jrufinalproject5.dto.user.UserRegisterIn;
import com.javarush.jrufinalproject5.entity.Role;
import com.javarush.jrufinalproject5.entity.User;
import com.javarush.jrufinalproject5.repository.UserRepository;
import com.javarush.jrufinalproject5.security.JwtUtils;
import jakarta.persistence.EntityExistsException;
import lombok.AllArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@AllArgsConstructor
@Transactional
public class AuthService {
    private final AuthenticationManager authenticationManager;
    private final JwtUtils jwtUtils;
    private UserRepository userRepository;
    private UserDto mapper;
    private PasswordEncoder passwordEncoder;

    public String registerUser(UserRegisterIn user) {
        User newUser = mapper.from(user);
        userRepository.findByLogin(newUser.getLogin()).ifPresent(user1 -> {
            throw new EntityExistsException("User '" + user.getLogin() + "' already exists");
        });
        newUser.setRole(Role.USER);
        newUser.setPassword(passwordEncoder.encode(newUser.getPassword()));
        userRepository.save(newUser);
        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(newUser.getLogin(), user.getPassword())
        );
        return jwtUtils.generateToken(newUser.getLogin());
    }

    public String login(UserLogIn user) {
        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(user.getLogin(), user.getPassword())
        );
        return jwtUtils.generateToken(user.getLogin());
    }
}
