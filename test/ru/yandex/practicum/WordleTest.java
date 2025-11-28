package ru.yandex.practicum;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;
import java.io.*;
import java.nio.file.Path;

class WordleTest {

    @TempDir
    Path tempDir;
    private final PrintWriter logWriter = new PrintWriter(new StringWriter());

    @Test
    void testMainMethod() throws IOException {
        File dictionaryFile = new File(tempDir.toFile(), "words_ru.txt");
        try (PrintWriter writer = new PrintWriter(dictionaryFile)) {
            writer.println("слово");
            writer.println("метро");
            writer.println("парта");
            writer.println("столы");
        }

        Wordle.main(new String[]{});
    }
}