package com.javarush.jrufinalproject5.dto.task;

import com.javarush.jrufinalproject5.entity.User;
import com.javarush.jrufinalproject5.repository.UserRepository;
import org.springframework.stereotype.Component;

@Component
public class UserMapper {

    private final UserRepository userRepository;

    public UserMapper(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    public User map(Long id) {
        if (id == null) return null;
        return userRepository.findById(id).orElse(null);
    }

}