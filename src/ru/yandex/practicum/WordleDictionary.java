package ru.yandex.practicum;

import ru.yandex.practicum.exceptions.NotFoundSymbolForPosition;

import java.util.*;
import java.io.PrintWriter;

/*
этот класс содержит в себе список слов List<String>
    его методы похожи на методы списка, но учитывают особенности игры
    также этот класс может содержать рутинные функции по сравнению слов, букв и т.д.
 */
public class WordleDictionary {

    private final List<String> words;
    private static final int WORD_LENGTH = 5;
    private final PrintWriter logWriter;

    public WordleDictionary(List<String> words, PrintWriter logWriter) {
        this.words = new ArrayList<>();
        this.logWriter = logWriter;

        for (String word : words) {
            if (word.length() == WORD_LENGTH) {
                this.words.add(word);
            }
        }

        if (this.words.isEmpty()) {
            logWriter.println("Словарь пуст!");
            throw new IllegalArgumentException("Словарь не содержит слов подходящей длины");
        }

        logWriter.println("Загружено слов в словарь: " + this.words.size());
    }

    public String giveNewWordForAdvice(Map<Integer, Character> knownPositions,
                                       Set<Character> confirmedLetters,
                                       Set<Character> excludedLetters,
                                       Map<Character, Set<Integer>> wrongPositions,
                                       List<String> enteredWords) throws NotFoundSymbolForPosition {

        List<String> possibleWords = new ArrayList<>();

        wordLoop:
        for (String word : words) {
            if (enteredWords.contains(word)) {
                continue;
            }

            for (Map.Entry<Integer, Character> entry : knownPositions.entrySet()) {
                int position = entry.getKey();
                char expectedChar = entry.getValue();
                if (word.charAt(position) != expectedChar) {
                    continue wordLoop;
                }
            }

            for (char letter : confirmedLetters) {
                if (word.indexOf(letter) == -1) {
                    continue wordLoop;
                }
            }

            for (char letter : excludedLetters) {
                if (word.indexOf(letter) != -1) {
                    continue wordLoop;
                }
            }

            for (Map.Entry<Character, Set<Integer>> entry : wrongPositions.entrySet()) {
                char letter = entry.getKey();
                Set<Integer> wrongPos = entry.getValue();
                for (int position : wrongPos) {
                    if (word.charAt(position) == letter) {
                        continue wordLoop;
                    }
                }
            }

            possibleWords.add(word);
        }

        if (!possibleWords.isEmpty()) {
            Random random = new Random();
            return possibleWords.get(random.nextInt(possibleWords.size()));
        }

        throw new NotFoundSymbolForPosition("Не могу найти подходящее слово для подсказки.");
    }

    public boolean wordInDictionary(String userWord) {
        boolean contains = words.contains(userWord);
        if (!contains) {
            logWriter.println("Слово не найдено в словаре: " + userWord);
        }
        return contains;
    }

    public String getRandomWord() {
        if (words.isEmpty()) {
            throw new IllegalStateException("Словарь пуст");
        }
        Random random = new Random();
        return words.get(random.nextInt(words.size()));
    }
}
