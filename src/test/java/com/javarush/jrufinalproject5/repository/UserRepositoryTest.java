package com.javarush.jrufinalproject5.repository;

import com.javarush.jrufinalproject5.entity.User;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.data.jpa.test.autoconfigure.DataJpaTest;
import org.springframework.boot.jdbc.test.autoconfigure.AutoConfigureTestDatabase;
import org.springframework.boot.testcontainers.service.connection.ServiceConnection;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;

import static com.javarush.jrufinalproject5.entity.Role.USER;
import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@Testcontainers
public class UserRepositoryTest {

    @Container
    @ServiceConnection
    static PostgreSQLContainer<?> postgresContainer = new PostgreSQLContainer<>("postgres:latest");
    @Autowired
    private UserRepository userRepository;
    private final List<User> initialUsersList = InitialDataBaseEntities.getInitialUsersList();

    @Test
    void findAllTest() {
        List<User> all = userRepository.findAll();
        assertEquals(initialUsersList, all);
    }

    @Test
    void findByIdTest() {
        User user = userRepository.findById(1L).orElseThrow();
        assertEquals(initialUsersList.get(0), user);
    }

    @Test
    void findByLoginTest() {
        String adminEmail = initialUsersList.get(0).getEmail();
        Optional<User> adminOpt = userRepository.findByLogin("admin");
        User admin = adminOpt.orElseThrow();
        assertNotNull(admin);
        assertEquals(adminEmail, admin.getEmail());
    }

    @Test
    void saveUserTest() {
        User testUser = InitialDataBaseEntities.getUser("testUser");
        User saved = userRepository.save(testUser);
        User test = userRepository.findById(saved.getId()).orElseThrow();
        assertNotNull(test);
        assertEquals(testUser, test);
        List<User> list = userRepository.findAll();
        assertEquals(initialUsersList.size() + 1, list.size());
    }

    @Test
    void updateUserTest() {
        User testUser = InitialDataBaseEntities.getUser("testUser");
        User testUserJohn = InitialDataBaseEntities.getUser("testUserJohn");
        long savedId = userRepository.save(testUser).getId();
        User test = userRepository.findById(savedId).orElseThrow();
        assertNotNull(test);
        test.setLogin(testUserJohn.getLogin());
        test.setPassword(testUserJohn.getPassword());
        test.setRole(USER);
        userRepository.save(test);

        User user = userRepository.findById(savedId).orElseThrow();
        assertEquals(testUserJohn.getLogin(), user.getLogin());
        assertEquals(testUserJohn.getPassword(), user.getPassword());
        assertEquals(testUserJohn.getEmail(), user.getEmail());
        assertEquals(testUserJohn.getRole(), user.getRole());
    }

    @Test
    void deleteUserTest() {
        User testUser = InitialDataBaseEntities.getUser("testUser");
        long savedId = userRepository.save(testUser).getId();
        List<User> list = userRepository.findAll();
        assertNotEquals(initialUsersList, list);
        assertEquals(initialUsersList.size() + 1, list.size());
        userRepository.deleteById(savedId);
        list = userRepository.findAll();
        assertEquals(initialUsersList, list);
        assertThrows(NoSuchElementException.class, () ->
                userRepository.findByLogin(testUser.getLogin()).orElseThrow());
    }
}
