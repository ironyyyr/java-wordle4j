package ru.yandex.practicum.exceptions;

import java.io.FileNotFoundException;

public class DictionaryFileNotFoundException extends FileNotFoundException {
    public DictionaryFileNotFoundException(String message) {
        super(message);
    }
}
