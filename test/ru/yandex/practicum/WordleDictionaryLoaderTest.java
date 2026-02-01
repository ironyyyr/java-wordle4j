package ru.yandex.practicum;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;
import ru.yandex.practicum.exceptions.DictionaryFileNotFoundException;
import ru.yandex.practicum.exceptions.DictionaryIsEmptyExeption;
import ru.yandex.practicum.implementations.WordleDictionaryLoader;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.nio.charset.UnsupportedCharsetException;
import java.nio.file.Files;
import java.nio.file.InvalidPathException;
import java.nio.file.Path;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class WordleDictionaryLoaderTest {

    @TempDir
    static Path tempDir;

    private WordleDictionaryLoader wordleDictionaryLoader;
    private Path dictionaryPath;
    private ByteArrayOutputStream logOutputStream;
    private PrintWriter logPrintWriter;

    @BeforeEach
    void setUp() throws IOException {
        dictionaryPath = tempDir.resolve("dictionaries");
        Files.createDirectories(dictionaryPath);

        logOutputStream = new ByteArrayOutputStream();
        logPrintWriter = new PrintWriter(logOutputStream, true);

        wordleDictionaryLoader = new WordleDictionaryLoader("UTF-8", dictionaryPath.toString(), logPrintWriter);
    }

    @AfterEach
    void tearDown() throws IOException {
        if (logPrintWriter != null) {
            logPrintWriter.close();
        }

        logOutputStream.close();
    }

    private String getLogInfo() {
        return logOutputStream.toString();
    }

    @Test
    void testLoadDictionarySuccess() throws IOException {
        String dictionaryName = "dictionary.txt";
        Path dictionaryFile = dictionaryPath.resolve(dictionaryName);
        List<String> words = List.of("собака", "кот", "слон");


        Files.write(dictionaryFile, words, StandardCharsets.UTF_8);

        List<String> result = wordleDictionaryLoader.loadDictionary(dictionaryName);

        assertAll(
                () -> assertEquals(result.size(), words.size(), "Количество слов не совпадает"),
                () -> assertIterableEquals(result, words, "Порядок слов не совпадает"),
                () -> assertTrue(getLogInfo().isEmpty(), "Лог должен быть пустым")
        );
    }

    @Test
    void testLoadDictionaryWithEmptyDictionaryException() throws IOException {
        String dictionaryName = "dictionary.txt";
        Path dictionaryFile = dictionaryPath.resolve(dictionaryName);
        List<String> words = List.of();

        Files.write(dictionaryFile, words, StandardCharsets.UTF_8);

        assertThrows(DictionaryIsEmptyExeption.class, () ->
                wordleDictionaryLoader.loadDictionary(dictionaryName));

        assertTrue(getLogInfo().contains("Словарь пустой. Наполните его."),
                "В логе отсутствует сообщение об исключении");
    }

    @Test
    void testLoadDictionaryWithFileNotFoundException() throws IOException {
        assertThrows(FileNotFoundException.class, () ->
                wordleDictionaryLoader.loadDictionary("test.txt"));

        assertTrue(getLogInfo().contains("Файла словаря test.txt не существует."), "Лог некорректно записался");
    }

    @Test
    void testLoadDictionaryWithInvalidPath() throws IOException {
        WordleDictionaryLoader runtimeExceptionDictionaryLoader = new WordleDictionaryLoader(
                "UTF-8",
                "non:exiting/path/",
                logPrintWriter
        );

        assertThrows(DictionaryFileNotFoundException.class, () ->
                runtimeExceptionDictionaryLoader.loadDictionary("test.jpg"));

        assertTrue(getLogInfo().contains("Некорректный путь к файлу."),
                "Должно быть /" + getLogInfo()
        );
    }

    @Test
    void testLoadDictionaryWithIllegalCharset() throws IOException {
        WordleDictionaryLoader runtimeExceptionDictionaryLoader = new WordleDictionaryLoader(
                "UTF-111",
                "dictionary",
                logPrintWriter
        );

        assertThrows(UnsupportedCharsetException.class, () ->
                runtimeExceptionDictionaryLoader.loadDictionary("test.jpg"));
        assertTrue(getLogInfo().contains("Неподдерживаемая кодировка UTF-111"), "Сообщение в лог записано некорректно");
    }


}
