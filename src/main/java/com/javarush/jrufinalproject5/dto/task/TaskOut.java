package com.javarush.jrufinalproject5.dto.task;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class TaskOut {
    private Long id;
    private String title;
    private String description;
    private LocalDateTime deadline;
    private String status;
}
