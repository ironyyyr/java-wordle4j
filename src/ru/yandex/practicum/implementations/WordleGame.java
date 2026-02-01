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
    private final LinkedHashMap<Character, Integer> answeredCharacters;
    private final LinkedHashMap<Integer, Character> answerSymbols;
    private final LinkedHashMap<Integer, Boolean> isSymbolAnswered;
    private final HashSet<Character> isSymbolFoundInWord;
    private final PrintWriter logOutput;

    public WordleGame(String answer, WordleDictionary dictionary, PrintWriter logOutput) {
        this.answer = answer;
        this.steps = 0;
        this.dictionary = dictionary;
        this.logOutput = logOutput;
        this.answeredCharacters = dictionary.countChars(answer);
        this.answerSymbols = fillHashMapSymbolPos();
        this.isSymbolAnswered = fillHashMapIsSymbol();
        this.isSymbolFoundInWord = new HashSet<>();
    }

    private LinkedHashMap<Integer, Character> fillHashMapSymbolPos() {
        LinkedHashMap<Integer, Character> linkedHashMap = new LinkedHashMap<>();
        for (int i = 0; i < answer.length(); i++) {
            Character character = answer.charAt(i);
            linkedHashMap.put(i, character);
        }
        return linkedHashMap;
    }

    private LinkedHashMap<Integer, Boolean> fillHashMapIsSymbol() {
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
    }

    public String wordleRound(String userAnswer) {

        userAnswer = dictionary.normalizeWord(userAnswer);
        String resultOfCompare = dictionary.compareWords(answer, userAnswer);

        for (int i = 0; i < answer.length(); i++) {
            char currentChar = answer.charAt(i);
            char currentCompareSymbol = resultOfCompare.charAt(i);

            if (currentCompareSymbol == '+') {
                dictionary.updateAnswerHashMap(answeredCharacters, currentChar);
                isSymbolFoundInWord.add(currentChar);
                isSymbolAnswered.put(i, true);
            } else if (currentCompareSymbol == '^') {
                isSymbolFoundInWord.add(currentChar);
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

    public int findWordToAdvice() {
        Character characterToAdvice = ' ';
        int characterToAdvicePosition = -1;
        for (Integer key : isSymbolAnswered.keySet()) {
            if (!isSymbolAnswered.get(key)) {
                characterToAdvice = answerSymbols.get(key);
                characterToAdvicePosition = key;
                isSymbolAnswered.put(key, true);
                isSymbolFoundInWord.add(characterToAdvice);
                break;
            }
        }

        updateLinkedHashMap(answeredCharacters, characterToAdvice);

        return characterToAdvicePosition;
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
        String advice = "";
        int characterPosToAdvice = findWordToAdvice();

        for (String word : dictionary.getWords()) {
            for (int i = 0; i < word.length(); i++) {
                if (
                        word.charAt(characterPosToAdvice) == answer.charAt(characterPosToAdvice) &&
                                word.matches(createStrictRegExp()) &&
                                word.matches(createSoftRegExp())
                ) {
                    advice = word;
                }
            }
        }

        StringBuilder replacedCharacterToUpperCase = new StringBuilder(advice);
        replacedCharacterToUpperCase.setCharAt(characterPosToAdvice,
                Character.toUpperCase(advice.charAt(characterPosToAdvice)));

        return replacedCharacterToUpperCase.toString();
    }

    public String createStrictRegExp() {
        StringBuilder stringBuilder = new StringBuilder();
        for (int i = 0; i < answer.length(); i++) {
            if (isSymbolAnswered.get(i)) {
                stringBuilder.append(answer.charAt(i));
            } else {
                stringBuilder.append(".{1}");
            }
        }
        return stringBuilder.toString();
    }

    public String createSoftRegExp() {
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append("^");

        for (int i = 0; i < answer.length(); i++) {
            if (isSymbolFoundInWord.contains(answer.charAt(i))) {
                stringBuilder.append("(?=.*").append(answer.charAt(i)).append(")");
            }
        }

        stringBuilder.append(".*$");
        return stringBuilder.toString();
    }

    public String getAnswer() {
        return answer;
    }
}
