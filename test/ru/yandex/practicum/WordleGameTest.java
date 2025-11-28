package ru.yandex.practicum;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import ru.yandex.practicum.exceptions.EndGameException;
import ru.yandex.practicum.exceptions.WordCheckException;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class WordleGameTest {

    private WordleGame game;
    private WordleDictionary dictionary;
    private PrintWriter logWriter;

    @BeforeEach
    void setUp() {
        StringWriter stringWriter = new StringWriter();
        logWriter = new PrintWriter(stringWriter);

        List<String> testWords = Arrays.asList("слово", "метро", "парта", "столы", "крона");
        dictionary = new WordleDictionary(testWords, logWriter);
        game = new WordleGame(dictionary, logWriter);
    }

    @Test
    void testGameInitialization() {
        assertNotNull(game.getAnswer());
        assertEquals(5, game.getAnswer().length());
    }

    @Test
    void testCompareCorrectWord() {
        String answer = game.getAnswer();

        try {
            String result = game.compare(answer);
            assertEquals("+++++ (попытка 1/6)", result);
        } catch (EndGameException e) {
            assertTrue(e.getMessage().contains("Поздравляем"));
        } catch (WordCheckException e) {
            fail("Не должно быть исключения проверки слова");
        }
    }

    @Test
    void testCompareWrongLength() {
        assertThrows(WordCheckException.class,
                () -> game.compare("короткое"));
    }

    @Test
    void testCompareWordNotInDictionary() {
        assertThrows(WordCheckException.class,
                () -> game.compare("абвгд"));
    }

    @Test
    void testComparePartialMatch() throws Exception {
        String answer = game.getAnswer();
        String testWord = "слово";
        if (testWord.equals(answer)) {
            testWord = "метро";
        }

        String result = game.compare(testWord);
        assertNotNull(result);
        assertTrue(result.contains("попытка 1/6"));
    }

    @Test
    void testMaxStepsReached() {
        List<String> wordsToTry = Arrays.asList("слово", "метро", "парта", "столы", "крона", "слово");

        for (String word : wordsToTry) {
            if (!word.equals(game.getAnswer())) {
                try {
                    game.compare(word);
                } catch (EndGameException | WordCheckException e) {
                }
            }
        }

        assertThrows(EndGameException.class,
                () -> game.compare("метро"));
    }

    @Test
    void testGiveAdvice() throws Exception {
        try {
            game.compare("слово");
        } catch (EndGameException e) {
        }

        String advice = game.giveAdvice();
        assertNotNull(advice);
        assertTrue(advice.contains("Попробуйте слово:"));
    }

    @Test
    void testGameStateAfterMoves() throws Exception {
        String testWord = "слово";
        if (testWord.equals(game.getAnswer())) {
            testWord = "метро";
        }

        game.compare(testWord);


        assertDoesNotThrow(() -> game.giveAdvice());
    }
}