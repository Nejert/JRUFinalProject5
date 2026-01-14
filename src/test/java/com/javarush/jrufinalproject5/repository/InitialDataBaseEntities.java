package com.javarush.jrufinalproject5.repository;


import com.javarush.jrufinalproject5.entity.Task;
import com.javarush.jrufinalproject5.entity.User;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import static com.javarush.jrufinalproject5.entity.Role.ADMIN;
import static com.javarush.jrufinalproject5.entity.Role.USER;

public class InitialDataBaseEntities {
    private static final User admin = new User(1L, "admin", "admin", "admin@admin.com", ADMIN, null);
    private static final User user = new User(2L, "user", "user", "user@user.com", USER, null);
    private static final User test = new User(3L, "test", "test", "test@test.com", USER, null);
    private static final User some = new User(4L, "some", "some", "some@some.com", USER, null);

    private static final User testUser = new User(null, "testUser", "testUser", "test@user.com", ADMIN, null);
    private static final User testUserJohn = new User(null, "John Doe", "JohnDoe", "test@user.com", USER, null);

    private static final Task homework = new Task(
            1L,
            "Complete homework",
            "Finish math and science homework",
            LocalDateTime.parse("2026-12-01T00:00:00"),
            "PENDING", admin);
    private static final Task server = new Task(
            2L,
            "Fix server",
            "Resolve critical issue on production server",
            LocalDateTime.parse("2026-11-25T00:00:00"),
            "IN_PROGRESS", user);
    private static final Task testTask = new Task(
            null,
            "Test task",
            "Testing test task",
            LocalDateTime.parse("2026-12-04T00:00:00"),
            "SOME_STATUS", admin);

    static {
        admin.setTasks(List.of(homework));
        user.setTasks(List.of(server));
    }

    private static User copyUser(User u) {
        return new User(u.getId(), u.getLogin(), u.getPassword(), u.getEmail(), u.getRole(), u.getTasks());
    }

    private static Task copyTask(Task t) {
        return new Task(t.getId(), t.getTitle(), t.getDescription(), t.getDeadline(), t.getStatus(), t.getUser());
    }

    public static User getUser(String login) {
        return switch (login) {
            case "admin" -> copyUser(admin);
            case "user" -> copyUser(user);
            case "test" -> copyUser(test);
            case "some" -> copyUser(some);
            case "testUser" -> copyUser(testUser);
            case "testUserJohn" -> copyUser(testUserJohn);
            default -> throw new IllegalStateException("Unexpected value: " + login);
        };
    }

    public static Task getTask(String title) {
        return switch (title) {
            case "homework" -> copyTask(homework);
            case "server" -> copyTask(server);
            case "testTask" -> copyTask(testTask);
            default -> throw new IllegalStateException("Unexpected value: " + title);
        };
    }

    public static List<User> getInitialUsersList() {
        return new ArrayList<>(List.of(admin, user, test, some));
    }

    public static List<Task> getInitialTasksList() {
        return new ArrayList<>(List.of(homework, server));
    }
}
