package ru.yandex.practicum;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import ru.yandex.practicum.implementations.WordleDictionary;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class WordleDictionaryTest {
    private List<String> dirtyDictionary;
    private List<String> resultDictionary;
    private WordleDictionary wordleDictionary;
    private final String result = "класс";
    private final String halfResult = "сссаа";
    private final String badResult = "хобот";

    @BeforeEach
    void setUp() {
        dirtyDictionary = new ArrayList<>(List.of("кОтИК", "", "коТик  ", "КОТИК"));
        resultDictionary = new ArrayList<>(List.of("котик", "котик", "котик"));
        wordleDictionary = new WordleDictionary(dirtyDictionary);
    }

    @Test
    void testFormatDictionarySuccess() {
        assertIterableEquals(
                wordleDictionary.formatDictionary(dirtyDictionary),
                resultDictionary,
                "Массивы должны быть идентичны"
        );
    }

    @Test
    void testGetWordsSuccess() {
        assertIterableEquals(
                wordleDictionary.getWords(),
                resultDictionary,
                "Слова из словаря некорректно обрабатываются"
        );
    }

    @Test
    void testSuccessCompareWords() {
        assertTrue(() ->
                wordleDictionary.compareWords(result, result).equals("+".repeat(result.length()))
        );
    }

    @Test
    void testHalfCorrectCompareWords() {
        assertEquals("^^-^-",
                wordleDictionary.compareWords(result, halfResult),
                "Слова некорректно сравниваются"
        );
    }

    @Test
    void testBadCompareWords() {
        assertEquals("-----",
                wordleDictionary.compareWords(result, badResult),
                "Слова некорректно сравниваются");
    }


}
