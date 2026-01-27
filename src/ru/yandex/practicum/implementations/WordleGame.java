package ru.yandex.practicum.implementations;

import ru.yandex.practicum.exceptions.IncorrectWordFormat;
import ru.yandex.practicum.exceptions.WordNotFoundInDictionary;
import ru.yandex.practicum.exceptions.WordWasAlreadyUsed;

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

    private final String answer;
    private final WordleDictionary dictionary;
    private int steps;
    private final LinkedList<String> usedWords;
    private final LinkedHashMap<Character, Integer> answeredCharacters;
    private final LinkedHashMap<Integer, Character> answerSymbols;
    private final LinkedHashMap<Integer, Boolean> isSymbolAnswered;
    private PrintWriter logOutput;

    public WordleGame(String answer, WordleDictionary dictionary, PrintWriter logOutput) {
        this.answer = answer;
        this.steps = 0;
        this.dictionary = dictionary;
        this.usedWords = new LinkedList<>();
        this.logOutput = logOutput;
        this.answeredCharacters = dictionary.countChars(answer);
        this.answerSymbols = fillHashMapSymbolPos();
        this.isSymbolAnswered = fillHashMapIsSymbolUsed();
    }

    private LinkedHashMap<Integer, Character> fillHashMapSymbolPos() {
        LinkedHashMap<Integer, Character> linkedHashMap = new LinkedHashMap<>();
        for (int i = 0; i < answer.length(); i++) {
            Character character = answer.charAt(i);
            linkedHashMap.put(i, character);
        }
        return linkedHashMap;
    }

    private LinkedHashMap<Integer, Boolean> fillHashMapIsSymbolUsed() {
        LinkedHashMap<Integer, Boolean> linkedHashMap = new LinkedHashMap<>();
        for (int i = 0; i < answer.length(); i++) {
            linkedHashMap.put(i, false);
        }
        return linkedHashMap;
    }

    public void plusPlusStep() {
        steps++;
    }

    public int getSteps() {
        return steps;
    }

    public String playStep(String userAnswer) {
        validateUserAnswer(userAnswer);
        plusPlusStep();
        return wordleRound(userAnswer);
    }

    public void validateUserAnswer(String userAnswer) throws WordNotFoundInDictionary, IncorrectWordFormat, WordWasAlreadyUsed {
        if (!dictionary.isWordCorrectLength(userAnswer)) {
            logOutput.println("Введенное слово " + userAnswer + " некорректной длины.");
            throw new IncorrectWordFormat("Введенное слово " + userAnswer + " некорректной длины.");
        }

        if (!dictionary.isWordInDictionary(userAnswer)) {
            logOutput.println("Слово " + userAnswer + " не найдено в словаре.");
            throw new WordNotFoundInDictionary("Слово " + userAnswer + " не найдено в словаре.");
        }

        if (usedWords.contains(userAnswer)) {
            logOutput.println("Слово " + userAnswer + " уже было использовано.");
            throw new WordWasAlreadyUsed("Слово " + userAnswer + " уже было использовано.");
        }
    }

    public String wordleRound(String userAnswer) {
        usedWords.add(userAnswer);
        String resultOfCompare = dictionary.compareWords(answer, userAnswer);
        for (int i = 0; i < answer.length(); i++) {
            char currentChar = answer.charAt(i);
            char currentCompareSymbol = resultOfCompare.charAt(i);
            if (currentCompareSymbol == '+') {
                dictionary.updateAnswerHashMap(answeredCharacters, currentChar);
                isSymbolAnswered.put(i, true);
            }
        }
        return resultOfCompare;
    }

    public boolean isWordGuessed() {
        for (Integer key : isSymbolAnswered.keySet()) {
            if (!isSymbolAnswered.get(key)) {
                return false;
            }
        }

        return countUnguessedCharacters(answeredCharacters) == 0;
    }

    public String findWordToAdvice() {
        Character characterToAdvice = ' ';
        int characterToAdvicePosition = -1;
        for (Integer key : isSymbolAnswered.keySet()) {
            if (!isSymbolAnswered.get(key)) {
                characterToAdvice = answerSymbols.get(key);
                characterToAdvicePosition = key;
                isSymbolAnswered.put(key, true);
                break;
            }
        }

        updateLinkedHashMap(answeredCharacters, characterToAdvice);

        return dictionary.findAdviceWord(characterToAdvice, characterToAdvicePosition);
    }

    private void updateLinkedHashMap(LinkedHashMap<Character, Integer> userLinkedHashMap, Character character) {
        if (userLinkedHashMap.get(character) != 0) {
            dictionary.updateAnswerHashMap(answeredCharacters, character);
        }
    }

    private int countUnguessedCharacters(LinkedHashMap<Character, Integer> userLinkedHashMap) {
        int resulOfCount = 0;
        for (Character character : userLinkedHashMap.keySet()) {
            resulOfCount += userLinkedHashMap.get(character);
        }
        return resulOfCount;
    }

    public String giveAdvice() {
        String adviceWord = findWordToAdvice();
        while (usedWords.contains(adviceWord.toLowerCase())) {
            adviceWord = findWordToAdvice();
        }

        usedWords.add(adviceWord);
        return adviceWord;
    }

    public String getAnswer() {
        return answer;
    }
}
