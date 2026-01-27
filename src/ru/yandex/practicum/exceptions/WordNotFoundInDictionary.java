package ru.yandex.practicum.exceptions;

public class WordNotFoundInDictionary extends RuntimeException {
    public WordNotFoundInDictionary(String message) {
        super(message);
    }
}
