package ru.yandex.practicum.exceptions;

public class IncorrectWordFormat extends RuntimeException {
    public IncorrectWordFormat(String message) {
        super(message);
    }
}
