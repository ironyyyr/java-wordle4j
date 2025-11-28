package ru.yandex.practicum;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.io.*;
import java.nio.file.Path;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class WordleDictionaryLoaderTest {

    @TempDir
    Path tempDir;
    private PrintWriter logWriter;

    @BeforeEach
    void setUp() {
        StringWriter stringWriter = new StringWriter();
        logWriter = new PrintWriter(stringWriter);
    }

    @Test
    void testLoadDictionarySuccess() throws IOException {
        File dictionaryFile = new File(tempDir.toFile(), "words_ru.txt");
        try (PrintWriter writer = new PrintWriter(dictionaryFile, "UTF-8")) {
            writer.println("СЛОВО");
            writer.println("МЕТРО");
            writer.println("парта");
            writer.println("  столы  ");
            writer.println("крона");
        }

        WordleDictionaryLoader loader = new WordleDictionaryLoaderTestable(
                logWriter, dictionaryFile.getAbsolutePath());

        List<String> words = loader.getWordleDictionary();

        assertNotNull(words);
        assertFalse(words.isEmpty());
        assertEquals(5, words.size());
        assertTrue(words.contains("слово"));
        assertTrue(words.contains("метро"));
    }

    @Test
    void testNormalizeWord() throws IOException {
        File dictionaryFile = new File(tempDir.toFile(), "words_ru.txt");
        try (PrintWriter writer = new PrintWriter(dictionaryFile, "UTF-8")) {
            writer.println("ЁЛКА");
            writer.println("мёдный");
            writer.println("  СТОЛ  ");
        }

        WordleDictionaryLoader loader = new WordleDictionaryLoaderTestable(
                logWriter, dictionaryFile.getAbsolutePath());

        List<String> words = loader.getWordleDictionary();

        assertTrue(words.contains("елка"));
        assertTrue(words.contains("медный"));
        assertTrue(words.contains("стол"));
    }

    private static class WordleDictionaryLoaderTestable extends WordleDictionaryLoader {
        private final String customPath;

        public WordleDictionaryLoaderTestable(PrintWriter logWriter, String customPath) throws IOException {
            super(logWriter);
            this.customPath = customPath;
        }

        @Override
        public void loadDictionary() throws IOException {
            File dictionaryFile = new File(customPath);

            if (!dictionaryFile.exists()) {
                getLogWriter().println("Файл словаря не найден: " + dictionaryFile.getAbsolutePath());
                throw new IOException("Файл словаря не найден: " + dictionaryFile.getAbsolutePath());
            }

            try (BufferedReader reader = new BufferedReader(
                    new InputStreamReader(new FileInputStream(dictionaryFile), java.nio.charset.StandardCharsets.UTF_8))) {

                String line;
                int loadedWords = 0;

                while ((line = reader.readLine()) != null) {
                    String normalizedWord = normalizeWord(line.trim());
                    if (!normalizedWord.isEmpty()) {
                        getWordleDictionary().add(normalizedWord);
                        loadedWords++;
                    }
                }

                if (loadedWords == 0) {
                    throw new IOException("Словарь пуст после загрузки");
                }

            } catch (IOException e) {
                getLogWriter().println("Ошибка загрузки словаря: " + e.getMessage());
                throw e;
            }
        }

        @Override
        public List<String> getWordleDictionary() {
            return super.getWordleDictionary();
        }

        public PrintWriter getLogWriter() {
            return null; // заглушка
        }
    }
}