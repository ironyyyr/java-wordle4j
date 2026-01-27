package ru.yandex.practicum.implementations;

import java.util.*;

/*
этот класс содержит в себе список слов List<String>
    его методы похожи на методы списка, но учитывают особенности игры
    также этот класс может содержать рутинные функции по сравнению слов, букв и т.д.
 */
public class WordleDictionary {

    private static final int WORD_LENGTH = 5;

    private final List<String> words;

    public List<String> getWords() {
        return words;
    }

    public WordleDictionary(List<String> dirtyDictionary) {
        this.words = formatDictionary(dirtyDictionary);
    }

    public List<String> formatDictionary(List<String> dictionary) {
        List <String> words = new ArrayList<>();
        for (String word : dictionary) {
            if (word.isBlank() || word.trim().length() != WORD_LENGTH) {
                continue;
            }
            words.add(word.trim().toLowerCase());
        }
        return words;
    }

    public String compareWords(String answer, String userAnswer) {
        if (answer.equals(userAnswer)) {
            return "+".repeat(answer.length());
        }

        return compareChars(answer, userAnswer);
    }

    public String compareChars(String answer, String userAnswer) {
        StringBuilder stringBuilder = new StringBuilder();
        HashMap<Character, Integer> answerHashMap = countChars(answer);
        HashMap<Character, Integer> userAnswerHashMap = countChars(userAnswer);

        for (int i = 0; i < answer.length(); i++) {
            char answerChar = answer.charAt(i);
            char userAnswerChar = userAnswer.charAt(i);

            if (answerChar == userAnswerChar) {
                updateAnswerHashMap(userAnswerHashMap, userAnswerChar);
                updateAnswerHashMap(answerHashMap, userAnswerChar);
                stringBuilder.append("+");
                continue;
            }

            if (answerHashMap.containsKey(userAnswerChar) &&
                    userAnswerHashMap.get(userAnswerChar) != 0 &&
                    answerHashMap.get(userAnswerChar) != 0
            ) {
                updateAnswerHashMap(userAnswerHashMap, userAnswerChar);
                updateAnswerHashMap(answerHashMap, userAnswerChar);
                stringBuilder.append("^");
            } else {
                stringBuilder.append("-");
            }
        }
        return stringBuilder.toString();
    }

    public LinkedHashMap<Character, Integer> countChars(String word) {
        LinkedHashMap<Character, Integer> hashMap = new LinkedHashMap<>();
        for (int i = 0; i < word.length(); i++) {
            Character character = word.charAt(i);

            if (hashMap.containsKey(character)) {
                hashMap.put(character, hashMap.get(character) + 1);
                continue;
            }

            hashMap.put(character, 1);
        }
        return hashMap;
    }

    public void updateAnswerHashMap(HashMap<Character, Integer> userAnswerHashMap, char userAnswerChar) {
        if (userAnswerHashMap.get(userAnswerChar) != 0) {
            userAnswerHashMap.put(userAnswerChar, userAnswerHashMap.get(userAnswerChar) - 1);
        }
    }

    public boolean isWordInDictionary(String userAnswer) {
        return words.contains(userAnswer);
    }

    public boolean isWordCorrectLength(String userAnswer) {
        return userAnswer.length() == WORD_LENGTH;
    }

    public String findAdviceWord(Character characterToAdvice, int characterPosition) {
        String adviceWord = "";
        for (String word : words) {
            if (word.contains(characterToAdvice.toString()) && word.charAt(characterPosition) == characterToAdvice) {
                adviceWord = word;
            }
        }

        StringBuilder replacedCharacterToUpperCase = new StringBuilder(adviceWord);
        replacedCharacterToUpperCase.setCharAt(characterPosition, Character.toUpperCase(characterToAdvice));
        return replacedCharacterToUpperCase.toString();
    }

    public String getNewAnswer() {
        Random random = new Random();
        return words.get(random.nextInt(words.size()) - 1);
    }
}
