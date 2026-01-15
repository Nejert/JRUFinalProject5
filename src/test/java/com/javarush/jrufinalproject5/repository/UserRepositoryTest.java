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
        List<User> all = userRepository.findAll();
        assertEquals(InitialDataBaseEntities.USERS, all);
    }

    @Test
    void findByIdTest() {
        User user = userRepository.findById(1L).orElseThrow();
        assertEquals(InitialDataBaseEntities.ADMIN, user);
    }

    @Test
    void findByLoginTest() {
        User admin = userRepository.findByLogin("admin").orElseThrow();
        assertNotNull(admin);
        assertEquals(InitialDataBaseEntities.ADMIN, admin);
    }

    @Test
    void saveUserTest() {
        User testUser = getUser("saveUserTest");
        Long savedId = userRepository.save(testUser).getId();
        User test = userRepository.findById(savedId).orElseThrow();
        assertNotNull(test);
        assertEquals(testUser, test);
        List<User> list = userRepository.findAll();
        assertEquals(InitialDataBaseEntities.USERS.size() + 1, list.size());
        deleteFromDB(test);
    }

    @Test
    void updateUserTest() {
        User testUser = getUser("updateUserTest");
        long savedId = userRepository.save(testUser).getId();
        User test = userRepository.findById(savedId).orElseThrow();
        assertNotNull(test);
        test.setLogin("login");
        test.setRole(Role.USER);
        userRepository.save(test);
        User user = userRepository.findById(savedId).orElseThrow();
        assertEquals("login", user.getLogin());
        assertEquals(Role.USER, user.getRole());
        deleteFromDB(user);
    }

    @Test
    void deleteUserTest() {
        User testUser = getUser("deleteUserTest");
        User saved = userRepository.save(testUser);
        List<User> list = userRepository.findAll();
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
