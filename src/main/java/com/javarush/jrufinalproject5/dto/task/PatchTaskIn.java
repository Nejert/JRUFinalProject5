package com.javarush.jrufinalproject5.dto.task;


import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class PatchTaskIn {
    @Positive
    private Long id;
    @Size(min = 3, max = 100)
    private String title;
    private String description;
    private LocalDateTime deadline;
    private String status;
    private Long userId;
}
