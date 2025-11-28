package ru.yandex.practicum;

import ru.yandex.practicum.exceptions.EndGameException;
import ru.yandex.practicum.exceptions.NotFoundSymbolForPosition;
import ru.yandex.practicum.exceptions.WordCheckException;

import java.io.PrintWriter;
import java.util.*;

/*
в этом классе хранится словарь и состояние игры
    текущий шаг
    всё что пользователь вводил
    правильный ответ

в этом классе нужны методы, которые
    проанализируют совпадение слова с ответом
    предложат слово-подсказку с учётом всего, что вводил пользователь ранее

не забудьте про специальные типы исключений для игровых и неигровых ошибок
 */
public class WordleGame {

    private static final int MAX_STEPS = 6;
    private static final int WORD_LENGTH = 5;

    private final String answer;
    private int steps;
    private final WordleDictionary dictionary;
    private final List<String> enteredWords;
    private final PrintWriter logWriter;

    // Для хранения информации о позициях
    private final Map<Integer, Character> knownPositions;
    private final Set<Character> confirmedLetters;
    private final Set<Character> excludedLetters;
    private final Map<Character, Set<Integer>> wrongPositions;

    public WordleGame(WordleDictionary wordleDictionary, PrintWriter logWriter) {
        this.dictionary = wordleDictionary;
        this.logWriter = logWriter;
        this.enteredWords = new ArrayList<>();
        this.knownPositions = new HashMap<>();
        this.confirmedLetters = new HashSet<>();
        this.excludedLetters = new HashSet<>();
        this.wrongPositions = new HashMap<>();

        this.answer = dictionary.getRandomWord();
        this.steps = 0;

        logWriter.println("Новая игра начата. Ответ: " + answer);
    }

    public String compare(String userWord) throws EndGameException, WordCheckException {
        if (userWord.length() != WORD_LENGTH) {
            throw new WordCheckException("Слово должно содержать " + WORD_LENGTH + " букв.");
        }

        if (!dictionary.wordInDictionary(userWord)) {
            throw new WordCheckException("Слово '" + userWord + "' не найдено в словаре.");
        }

        enteredWords.add(userWord);
        steps++;

        StringBuilder result = new StringBuilder();
        char[] resultChars = new char[WORD_LENGTH];
        Arrays.fill(resultChars, '-');

        char[] answerArray = answer.toCharArray();
        boolean[] usedInAnswer = new boolean[WORD_LENGTH];

        for (int i = 0; i < WORD_LENGTH; i++) {
            if (userWord.charAt(i) == answerArray[i]) {
                resultChars[i] = '+';
                usedInAnswer[i] = true;
                knownPositions.put(i, userWord.charAt(i));
                confirmedLetters.add(userWord.charAt(i));
            }
        }

        for (int i = 0; i < WORD_LENGTH; i++) {
            if (resultChars[i] == '+') continue;

            char currentChar = userWord.charAt(i);
            boolean found = false;

            for (int j = 0; j < WORD_LENGTH; j++) {
                if (!usedInAnswer[j] && answerArray[j] == currentChar) {
                    resultChars[i] = '^';
                    usedInAnswer[j] = true;
                    found = true;
                    confirmedLetters.add(currentChar);
                    wrongPositions.computeIfAbsent(currentChar, k -> new HashSet<>()).add(i);
                    break;
                }
            }

            if (!found) {
                resultChars[i] = '-';
                excludedLetters.add(currentChar);
            }
        }

        String resultStr = new String(resultChars);
        logWriter.println("Ход " + steps + ": " + userWord + " -> " + resultStr);

        if (resultStr.equals("+++++")) {
            throw new EndGameException("Поздравляем! Вы угадали слово '" + answer + "!");
        }

        if (steps >= MAX_STEPS) {
            throw new EndGameException("Игра окончена. У вас закончились попытки. Загаданное слово: " + answer);
        }

        return resultStr + " (попытка " + steps + "/" + MAX_STEPS + ")";
    }

    public String giveAdvice() throws NotFoundSymbolForPosition, EndGameException {
        try {
            String advice = dictionary.giveNewWordForAdvice(
                    knownPositions, confirmedLetters, excludedLetters, wrongPositions, enteredWords);

            enteredWords.add(advice);

            logWriter.println("Дана подсказка: " + advice);

            return "Попробуйте слово: " + advice;

        } catch (NotFoundSymbolForPosition e) {
            logWriter.println("Не удалось дать подсказку: " + e.getMessage());
            throw e;
        }
    }

    public String getAnswer() {
        return answer;
    }
}
