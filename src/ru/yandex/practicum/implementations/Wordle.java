package ru.yandex.practicum.implementations;

import ru.yandex.practicum.exceptions.*;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.PrintWriter;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Scanner;

/*
в главном классе нам нужно:
    создать лог-файл (он должен передаваться во все классы)
    создать загрузчик словарей WordleDictionaryLoader
    загрузить словарь WordleDictionary с помощью класса WordleDictionaryLoader
    затем создать игру WordleGame и передать ей словарь
    вызвать игровой метод в котором в цикле опрашивать пользователя и передавать информацию в игру
    вывести состояние игры и конечный результат
 */
public class Wordle {

    private static final String WORKING_PATH = "C:\\Users\\ivan.ivashkin\\IdeaProjects\\java-wordle4j";
    private static final String DICTIONARY_FILE_NAME = "words_ru.txt";
    private static final String ENCODING = "UTF-8";
    private static final String LOGFILE_NAME = "logfile.txt";
    private static final int MAX_STEPS = 6;
    private static final int WORD_LENGTH = 5;

    public static void main(String[] args) {
        Path logFilePath = Path.of(WORKING_PATH, LOGFILE_NAME);
        try {
            if (!Files.exists(logFilePath)) {
                logFilePath = Files.createFile(logFilePath);
            }
        } catch (IOException ioException) {
            System.err.println("Лог файл " + LOGFILE_NAME + " по пути " + WORKING_PATH + " не удалось создать.");
            ioException.printStackTrace();
        }

        try (PrintWriter logPrintWriter = new PrintWriter(logFilePath.toFile())) {
            WordleDictionaryLoader wordleDictionaryLoader = new WordleDictionaryLoader(
                    ENCODING,
                    WORKING_PATH,
                    logPrintWriter
            );
            WordleDictionary wordleDictionary = new WordleDictionary(
                    wordleDictionaryLoader.loadDictionary(DICTIONARY_FILE_NAME)
            );

            WordleGame wordleGame = new WordleGame(
                    wordleDictionary.getNewAnswer(),
                    wordleDictionary,
                    logPrintWriter
            );

            Scanner scanner = new Scanner(System.in);
            System.out.println("Для начала игры введите слово. Максимальная длина слова - " + WORD_LENGTH);
            System.out.println("Для получения подсказки введите Enter");
            String userAnswer;

            while (true) {
                userAnswer = scanner.nextLine();
                if (userAnswer.isBlank()) {
                    System.out.println(wordleGame.giveAdvice());
                } else {
                    try {
                        System.out.println(wordleGame.playStep(userAnswer));
                    } catch (IncorrectWordFormat incorrectWordFormat) {
                        System.out.println("Длина слова должна быть равна 5 символам.");
                    } catch (WordNotFoundInDictionary wordNotFoundInDictionary) {
                        System.out.println(wordNotFoundInDictionary.getMessage());
                    } catch (WordWasAlreadyUsed wordWasAlreadyUsed) {
                        System.out.println(wordWasAlreadyUsed.getMessage());
                    }
                }

                if (wordleGame.getSteps() > MAX_STEPS && !wordleGame.isWordGuessed()) {
                    System.out.println("Вы проиграли. Ответ - " + wordleGame.getAnswer());
                    break;
                }

                if (wordleGame.isWordGuessed()) {
                    System.out.println("Вы выиграли. Ответ - " + wordleGame.getAnswer());
                    break;
                }
            }
        } catch (DictionaryFileNotFoundException dictionaryFileNotFoundException) {
            System.err.println(dictionaryFileNotFoundException.getMessage());
        } catch (FileNotFoundException fileNotFoundException) {
            System.err.println();
        }

    }

}
