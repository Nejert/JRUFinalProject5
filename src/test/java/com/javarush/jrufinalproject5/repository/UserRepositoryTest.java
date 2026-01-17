package com.javarush.jrufinalproject5.repository;

import com.javarush.jrufinalproject5.config.Container;
import com.javarush.jrufinalproject5.entity.Role;
import com.javarush.jrufinalproject5.entity.User;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.data.jpa.test.autoconfigure.DataJpaTest;

import java.util.List;
import java.util.NoSuchElementException;

import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
public class UserRepositoryTest extends Container {
    @Autowired
    private UserRepository userRepository;

    @Test
    void findAllTest() {
        // When
        List<User> all = userRepository.findAll();
        // Then
        assertEquals(InitialDataBaseEntities.USERS, all);
    }

    @Test
    void findByIdTest() {
        // When
        User user = userRepository.findById(1L).orElseThrow();
        // Then
        assertEquals(InitialDataBaseEntities.ADMIN, user);
    }

    @Test
    void findByLoginTest() {
        // When
        User admin = userRepository.findByLogin("admin").orElseThrow();
        // Then
        assertNotNull(admin);
        assertEquals(InitialDataBaseEntities.ADMIN, admin);
    }

    @Test
    void saveUserTest() {
        // Given
        User testUser = getUser("saveUserTest");
        // When
        Long savedId = userRepository.save(testUser).getId();
        User test = userRepository.findById(savedId).orElseThrow();
        // Then
        assertNotNull(test);
        assertEquals(testUser, test);
        List<User> list = userRepository.findAll();
        assertEquals(InitialDataBaseEntities.USERS.size() + 1, list.size());
        deleteFromDB(test);
    }

    @Test
    void updateUserTest() {
        // Given
        User testUser = getUser("updateUserTest");
        // When
        long savedId = userRepository.save(testUser).getId();
        User test = userRepository.findById(savedId).orElseThrow();
        assertNotNull(test);
        test.setLogin("login");
        test.setRole(Role.USER);
        userRepository.save(test);
        // Then
        User user = userRepository.findById(savedId).orElseThrow();
        assertEquals("login", user.getLogin());
        assertEquals(Role.USER, user.getRole());
        deleteFromDB(user);
    }

    @Test
    void deleteUserTest() {
        // Given
        User testUser = getUser("deleteUserTest");
        // When
        User saved = userRepository.save(testUser);
        List<User> list = userRepository.findAll();
        // Then
        assertNotEquals(InitialDataBaseEntities.USERS, list);
        assertEquals(InitialDataBaseEntities.USERS.size() + 1, list.size());
        deleteFromDB(saved);
        list = userRepository.findAll();
        assertEquals(InitialDataBaseEntities.USERS, list);
    }

    private User getUser(String login) {
        return new User(
                null,
                login,
                login,
                "test@user.com",
                Role.ADMIN, null);
    }

    private void deleteFromDB(User user) {
        userRepository.deleteById(user.getId());
        assertThrows(NoSuchElementException.class, () ->
                userRepository.findByLogin(user.getLogin()).orElseThrow());
    }
}
