package com.javarush.jrufinalproject5.dto.user;

import com.javarush.jrufinalproject5.entity.Role;
import lombok.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class UserOut {
    private Long id;
    private String login;
    private String email;
    private Role role;
}
