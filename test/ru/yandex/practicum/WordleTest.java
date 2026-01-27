package ru.yandex.practicum;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;
import ru.yandex.practicum.exceptions.IncorrectWordFormat;
import ru.yandex.practicum.exceptions.WordNotFoundInDictionary;
import ru.yandex.practicum.implementations.WordleDictionary;
import ru.yandex.practicum.implementations.WordleDictionaryLoader;
import ru.yandex.practicum.implementations.WordleGame;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.PrintWriter;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class WordleTest {
    @TempDir
    static Path tempDir;

    private Path dictionaryPath;
    private ByteArrayOutputStream logOutputStream;
    private PrintWriter logPrintWriter;
    private WordleGame wordleGame;
    private WordleDictionary wordleDictionary;
    private final String answer = "кошка";
    private final String userAnswer = "котик";
    private final String userAdvice = "каШпо";

    @BeforeEach
    void setUp() throws IOException {
        dictionaryPath = tempDir.resolve("dictionaries");
        Files.createDirectories(dictionaryPath);

        logOutputStream = new ByteArrayOutputStream();
        logPrintWriter = new PrintWriter(logOutputStream, true);

        List<String> dictionary = new ArrayList<>(List.of("кошка", "котик", "арбуз", "ягода", "кашпо"));
        wordleDictionary = new WordleDictionary(dictionary);
        wordleGame = new WordleGame(answer, wordleDictionary, logPrintWriter);
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
    void testValidateAnswerSuccess() {
        wordleGame.validateUserAnswer(userAnswer);
    }

    @Test
    void testValidateAnswerIncorrectWordLength() {
        assertThrows(IncorrectWordFormat.class,
                () -> wordleGame.validateUserAnswer("аааааа"),
                "Длина слова некорректно оценивается");
        assertTrue(getLogInfo().contains("Введенное слово аааааа некорректной длины."),
                "Ошибка " + IncorrectWordFormat.class + " некорректно пишется в лог.");
    }

    @Test
    void testValidateAnswerNotFoundWordInDictionary() {
        assertThrows(WordNotFoundInDictionary.class,
                () -> wordleGame.validateUserAnswer("ааааа"),
                "Слово некорректно ищется в словаре");
        assertTrue(getLogInfo().contains("Слово ааааа не найдено в словаре."),
                "Ошибка " + IncorrectWordFormat.class + " некорректно пишется в лог.");
    }

    @Test
    void testGiveAdviceSuccess() {
        wordleGame.playStep(userAnswer);
        assertEquals(userAdvice, wordleGame.giveAdvice(), "Игровой совет дается некорректно.");
    }

    @Test
    void testPlayStepSuccess() {
        wordleGame.playStep(answer);
        assertTrue(wordleGame.isWordGuessed(), "Статус игры неправильно оценивается.");
    }

    @Test
    void testPlayStepBad() {
        wordleGame.playStep(userAnswer);
        assertFalse(wordleGame.isWordGuessed(), "Статус игры неправильно оценивается.");
    }

    @Test
    void testGetSteps() {
        wordleGame.playStep(userAnswer);
        assertEquals(wordleGame.getSteps(), 1, "Количество ходов в игре некорректно оценивается.");
    }



}
