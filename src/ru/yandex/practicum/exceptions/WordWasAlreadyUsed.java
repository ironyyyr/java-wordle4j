package ru.yandex.practicum.exceptions;

public class WordWasAlreadyUsed extends RuntimeException {
    public WordWasAlreadyUsed(String message) {
        super(message);
    }
}
