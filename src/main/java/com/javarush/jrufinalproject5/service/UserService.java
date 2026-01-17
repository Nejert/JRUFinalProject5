package com.javarush.jrufinalproject5.service;

import com.javarush.jrufinalproject5.dto.UserDto;
import com.javarush.jrufinalproject5.dto.user.PatchUserIn;
import com.javarush.jrufinalproject5.dto.user.UserIn;
import com.javarush.jrufinalproject5.dto.user.UserOut;
import com.javarush.jrufinalproject5.entity.User;
import com.javarush.jrufinalproject5.repository.UserRepository;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.NoSuchElementException;
import java.util.stream.Collectors;

@Service
@AllArgsConstructor
public class UserService {
    private final UserRepository userRepository;
    private final UserDto mapper;

    public List<UserOut> getAllUsers() {
        return userRepository
                .findAll()
                .stream()
                .map(mapper::from)
                .collect(Collectors.toList());
    }

    public UserOut findById(long id) {
        return userRepository
                .findById(id)
                .map(mapper::from)
                .orElseThrow();
    }

    public UserOut createUser(UserIn user) {
        return mapper.from(userRepository.save(mapper.from(user)));
    }

    public UserOut updateUser(Long id, UserIn user) {
        userRepository.findById(id).orElseThrow(() -> new NoSuchElementException("User not found!"));
        if (user.getId() == null || !id.equals(user.getId())) user.setId(id);
        return mapper.from(userRepository.save(mapper.from(user)));
    }

    public UserOut patchUpdateUser(Long id, PatchUserIn user) {
        User dbUser = userRepository.findById(id).orElseThrow(() -> new NoSuchElementException("User not found!"));
        if (user.getLogin() != null) {
            dbUser.setLogin(user.getLogin());
        }
        if (user.getPassword() != null) {
            dbUser.setPassword(user.getPassword());
        }
        if (user.getEmail() != null) {
            dbUser.setEmail(user.getEmail());
        }
        if (user.getRole() != null) {
            dbUser.setRole(user.getRole());
        }
        return mapper.from(userRepository.save(dbUser));
    }

    public void deleteUser(Long id) {
        userRepository.findById(id).orElseThrow(() -> new NoSuchElementException("User not found!"));
        userRepository.deleteById(id);
    }
}
