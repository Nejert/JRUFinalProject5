package com.javarush.jrufinalproject5.dto.user;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class UserRegisterIn {
    @NotNull
    @Size(min = 3, max = 50)
    private String login;
    @NotNull
    @Size(min = 3, max = 50)
    private String password;
    @Email
    private String email;
}
