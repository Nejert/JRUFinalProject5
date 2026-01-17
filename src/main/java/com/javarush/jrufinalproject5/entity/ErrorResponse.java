package com.javarush.jrufinalproject5.entity;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.List;

@Getter
@AllArgsConstructor
public class ErrorResponse {
    private int code;
    private List<String> messages;
}
