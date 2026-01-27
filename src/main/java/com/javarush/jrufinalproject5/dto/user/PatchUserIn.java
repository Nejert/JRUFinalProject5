package com.javarush.jrufinalproject5.dto.user;

import com.javarush.jrufinalproject5.entity.Role;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class PatchUserIn {
    @Positive
    private Long id;
    @Size(min = 3, max = 50)
    private String login;
    @Size(min = 3, max = 50)
    private String password;
    @Email
    private String email;
    private Role role;
}
