package ru.yandex.practicum;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import ru.yandex.practicum.exceptions.NotFoundSymbolForPosition;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;

class WordleDictionaryTest {

    private WordleDictionary dictionary;
    private PrintWriter logWriter;
    private List<String> testWords;

    @BeforeEach
    void setUp() {
        StringWriter stringWriter = new StringWriter();
        logWriter = new PrintWriter(stringWriter);

        testWords = Arrays.asList("слово", "метро", "парта", "столы", "крона");
        dictionary = new WordleDictionary(testWords, logWriter);
    }

    @Test
    void testConstructorWithValidWords() {
        assertEquals(5, dictionary.getRandomWord().length());
    }

    @Test
    void testConstructorWithEmptyList() {
        StringWriter sw = new StringWriter();
        PrintWriter pw = new PrintWriter(sw);

        assertThrows(IllegalArgumentException.class,
                () -> new WordleDictionary(Collections.emptyList(), pw));
    }

    @Test
    void testConstructorFiltersShortWords() {
        StringWriter sw = new StringWriter();
        PrintWriter pw = new PrintWriter(sw);

        List<String> mixedWords = Arrays.asList("слово", "кот", "дом", "метро");
        WordleDictionary dict = new WordleDictionary(mixedWords, pw);

        assertTrue(dict.wordInDictionary("слово"));
        assertTrue(dict.wordInDictionary("метро"));
    }

    @Test
    void testWordInDictionary() {
        assertTrue(dictionary.wordInDictionary("слово"));
        assertFalse(dictionary.wordInDictionary("несуществующее"));
    }

    @Test
    void testGetRandomWord() {
        String randomWord = dictionary.getRandomWord();
        assertNotNull(randomWord);
        assertEquals(5, randomWord.length());
        assertTrue(testWords.contains(randomWord));
    }

    @Test
    void testGiveNewWordForAdvice() throws NotFoundSymbolForPosition {
        Map<Integer, Character> knownPositions = new HashMap<>();
        knownPositions.put(0, 'с');

        Set<Character> confirmedLetters = new HashSet<>();
        confirmedLetters.add('л');

        Set<Character> excludedLetters = new HashSet<>();
        excludedLetters.add('х');

        Map<Character, Set<Integer>> wrongPositions = new HashMap<>();
        wrongPositions.put('о', new HashSet<>(List.of(4)));

        List<String> enteredWords = List.of("слово");

        String advice = dictionary.giveNewWordForAdvice(
                knownPositions, confirmedLetters, excludedLetters, wrongPositions, enteredWords);

        assertNotNull(advice);
        assertEquals(5, advice.length());
        assertEquals('с', advice.charAt(0));
        assertTrue(advice.contains("л"));
        assertFalse(advice.contains("х"));
        assertNotEquals("слово", advice);
    }

    @Test
    void testGiveNewWordForAdviceNoWordsFound() {
        Map<Integer, Character> knownPositions = new HashMap<>();
        knownPositions.put(0, 'я');

        Set<Character> confirmedLetters = new HashSet<>();
        Set<Character> excludedLetters = new HashSet<>();
        Map<Character, Set<Integer>> wrongPositions = new HashMap<>();
        List<String> enteredWords = new ArrayList<>();

        assertThrows(NotFoundSymbolForPosition.class,
                () -> dictionary.giveNewWordForAdvice(
                        knownPositions, confirmedLetters, excludedLetters, wrongPositions, enteredWords));
    }
}