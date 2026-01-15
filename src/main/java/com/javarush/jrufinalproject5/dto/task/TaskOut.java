package com.javarush.jrufinalproject5.dto.task;

import lombok.*;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class TaskOut {
    private Long id;
    private String title;
    private String description;
    private LocalDateTime deadline;
    private String status;
}
