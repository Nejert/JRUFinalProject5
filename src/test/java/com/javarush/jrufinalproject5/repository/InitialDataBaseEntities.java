package com.javarush.jrufinalproject5.repository;


import com.javarush.jrufinalproject5.entity.Role;
import com.javarush.jrufinalproject5.entity.Task;
import com.javarush.jrufinalproject5.entity.User;

import java.time.LocalDateTime;
import java.util.List;

public class InitialDataBaseEntities {
    public static final User ADMIN = new User(1L, "admin", "admin", "admin@admin.com", Role.ADMIN, null);
    public static final User USER = new User(2L, "user", "user", "user@user.com", Role.USER, null);
    public static final User TEST = new User(3L, "test", "test", "test@test.com", Role.USER, null);
    public static final User SOME = new User(4L, "some", "some", "some@some.com", Role.USER, null);

    public static final Task HOMEWORK = new Task(
            1L,
            "Complete homework",
            "Finish math and science homework",
            LocalDateTime.parse("2026-12-01T00:00:00"),
            "PENDING", ADMIN);
    public static final Task SERVER = new Task(
            2L,
            "Fix server",
            "Resolve critical issue on production server",
            LocalDateTime.parse("2026-11-25T00:00:00"),
            "IN_PROGRESS", USER);

    public static final List<User> USERS;
    public static final List<Task> TASKS;

    static {
        ADMIN.setTasks(List.of(HOMEWORK));
        USER.setTasks(List.of(SERVER));
        USERS = List.of(ADMIN, USER, TEST, SOME);
        TASKS = List.of(HOMEWORK, SERVER);
    }
}
