package ru.yandex.practicum.implementations;

import ru.yandex.practicum.exceptions.DictionaryFileNotFoundException;
import ru.yandex.practicum.exceptions.DictionaryIsEmptyExeption;

import java.io.*;
import java.nio.charset.Charset;
import java.nio.charset.UnsupportedCharsetException;
import java.nio.file.Files;
import java.nio.file.InvalidPathException;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

/*
этот класс содержит в себе всю рутину по работе с файлами словарей и с кодировками
    ему нужны методы по загрузке списка слов из файла по имени файла
    на выходе должен быть класс WordleDictionary
 */
public class WordleDictionaryLoader {

    private final String encoding;
    private final String dictionaryPath;
    private final PrintWriter logOutput;

    public WordleDictionaryLoader(String encoding, String dictionaryPath, PrintWriter logOutput) {
        this.encoding = encoding;
        this.dictionaryPath = dictionaryPath;
        this.logOutput = logOutput;
    }

    public List<String> loadDictionary(String fileName) throws RuntimeException, DictionaryFileNotFoundException {
        Charset charset;
        List<String> dictionaryList = new ArrayList<>();

        try {
            charset = Charset.forName(encoding);
        } catch (UnsupportedCharsetException unsupportedCharsetException) {
            logOutput.println("Неподдерживаемая кодировка " + encoding);
            throw new UnsupportedCharsetException(unsupportedCharsetException.getMessage());
        }

        Path fullPath;
        try {
            fullPath = Path.of(dictionaryPath, fileName);
        } catch (InvalidPathException invalidPathException) {
            logOutput.println("Некорректный путь к файлу");
            throw new RuntimeException(invalidPathException.getMessage());
        }

        if (!Files.exists(fullPath)) {
            logOutput.println("Файла словаря " + fileName + " не существует.");
            throw new DictionaryFileNotFoundException("Файла словаря" + fileName + " не существует.");
        }

        try (BufferedReader bufferedReader = Files.newBufferedReader(fullPath,
                charset)) {
            String word = bufferedReader.readLine();
            while (word != null) {
                if (!word.isBlank()) {
                    dictionaryList.add(word);
                }
                word = bufferedReader.readLine();
            }

            if (dictionaryList.isEmpty()) {
                logOutput.println("Словарь пустой. Наполните его.");
                throw new DictionaryIsEmptyExeption("Словарь пустой. Наполните его.");
            }
        } catch (IOException e) {
            logOutput.println("Ошибка чтения из файла. " + e.getMessage());
            throw new RuntimeException(e.getMessage());
        }

        return dictionaryList;
    }
}
