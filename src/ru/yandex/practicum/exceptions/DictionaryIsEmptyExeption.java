package ru.yandex.practicum.exceptions;

public class DictionaryIsEmptyExeption extends RuntimeException {
    public DictionaryIsEmptyExeption(String message) {
        super(message);
    }
}
