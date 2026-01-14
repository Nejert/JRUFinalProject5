package com.javarush.jrufinalproject5.entity;

import com.javarush.jrufinalproject5.config.RoleConverter;
import jakarta.persistence.*;
import lombok.*;

import java.util.List;

@Entity
@Data
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "users")
@ToString(exclude = "tasks")
@EqualsAndHashCode(exclude = "tasks")
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(nullable = false, unique = true)
    private String login;
    @Column(nullable = false)
    private String password;
    @Column(nullable = false, unique = true)
    private String email;
    @Column(name = "role_id")
    @Convert(converter = RoleConverter.class)
    private Role role;
    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<Task> tasks;
}
