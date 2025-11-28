package ru.yandex.practicum;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

/*
этот класс содержит в себе всю рутину по работе с файлами словарей и с кодировками
    ему нужны методы по загрузке списка слов из файла по имени файла
    на выходе должен быть класс WordleDictionary
 */
public class WordleDictionaryLoader {

    private static final String DICTIONARY_PATH = "";
    private static final String DICTIONARY_FILENAME = "words_ru.txt";

    private final List<String> wordleDictionary;
    private final PrintWriter logWriter;

    public WordleDictionaryLoader(PrintWriter logWriter) throws IOException {
        this.logWriter = logWriter;
        this.wordleDictionary = new ArrayList<>();
        loadDictionary();
    }

    public void loadDictionary() throws IOException {
        File dictionaryFile = new File(DICTIONARY_PATH + DICTIONARY_FILENAME);

        if (!dictionaryFile.exists()) {
            logWriter.println("Файл словаря не найден: " + dictionaryFile.getAbsolutePath());
            throw new IOException("Файл словаря не найден: " + dictionaryFile.getAbsolutePath());
        }

        try (BufferedReader reader = new BufferedReader(
                new InputStreamReader(new FileInputStream(dictionaryFile), StandardCharsets.UTF_8))) {

            String line;
            int loadedWords = 0;

            while ((line = reader.readLine()) != null) {
                String normalizedWord = normalizeWord(line.trim());
                if (!normalizedWord.isEmpty()) {
                    wordleDictionary.add(normalizedWord);
                    loadedWords++;
                }
            }


            if (loadedWords == 0) {
                throw new IOException("Словарь пуст после загрузки");
            }

        } catch (IOException e) {
            logWriter.println("Ошибка загрузки словаря: " + e.getMessage());
            throw e;
        }
    }

    public String normalizeWord(String word) {
        return word.toLowerCase()
                .replace('ё', 'е')
                .trim();
    }

    public List<String> getWordleDictionary() {
        return new ArrayList<>(wordleDictionary);
    }
}
