package ru.yandex.practicum;

import ru.yandex.practicum.exceptions.EndGameException;
import ru.yandex.practicum.exceptions.NotFoundSymbolForPosition;
import ru.yandex.practicum.exceptions.WordCheckException;

import java.io.*;
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

    private static final String LOG_PATH = "logs/";
    private static final String LOG_NAME = "wordle_log.txt";

    public static void main(String[] args) {
        createLogDirectory();

        try (PrintWriter logWriter = new PrintWriter(new FileWriter(LOG_PATH + LOG_NAME, true))) {

            WordleDictionaryLoader wordleDictionaryLoader = new WordleDictionaryLoader(logWriter);
            WordleDictionary wordleDictionary = new WordleDictionary(wordleDictionaryLoader.getWordleDictionary(),
                    logWriter);
            WordleGame wordleGame = new WordleGame(wordleDictionary, logWriter);
            Scanner scanner = new Scanner(System.in);

            logWriter.println("Игра начата. Загаданное слово: " + wordleGame.getAnswer());

            System.out.println("Добро пожаловать в Wordle! У вас 6 попыток угадать слово из 5 букв.");
            System.out.println("Введите слово или нажмите Enter для подсказки:");

            while (true) {
                try {
                    String input = scanner.nextLine();

                    if (input == null || input.trim().isEmpty()) {
                        String advice = wordleGame.giveAdvice();
                        System.out.println("Подсказка: " + advice);
                        continue;
                    }

                    String currentWord = input.trim().toLowerCase();
                    String result = wordleGame.compare(currentWord);
                    System.out.println(result);

                } catch (EndGameException e) {
                    System.out.println(e.getMessage());
                    logWriter.println("Игра завершена: " + e.getMessage());
                    break;
                } catch (NotFoundSymbolForPosition e) {
                    System.out.println(e.getMessage());
                    logWriter.println("Ошибка подсказки: " + e.getMessage());
                } catch (WordCheckException e) {
                    System.out.println(e.getMessage());
                    logWriter.println("Проверка слова: " + e.getMessage());
                } catch (Exception e) {
                    logWriter.println("Неожиданная ошибка: " + e.getMessage());
                    e.printStackTrace(logWriter);
                }
            }

        } catch (IOException e) {
            System.err.println("Ошибка работы с лог-файлом: " + e.getMessage());
        }
    }

    private static void createLogDirectory() {
        try {
            Files.createDirectories(Path.of(LOG_PATH));
        } catch (IOException e) {
            System.err.println("Не удалось создать директорию для логов: " + e.getMessage());
        }
    }
}
