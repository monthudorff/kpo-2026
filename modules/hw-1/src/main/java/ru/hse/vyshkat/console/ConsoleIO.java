package ru.hse.vyshkat.console;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.PrintStream;
import java.io.Reader;
import java.io.UncheckedIOException;
import java.util.Objects;

/**
 * Ввод-вывод консоли с повторным запросом при некорректном вводе.
 * В режиме эха прочитанные строки печатаются: так демо-сценарий выглядит
 * как настоящая сессия оператора.
 */
public final class ConsoleIO {

    /** Источник строк: клавиатура или демо-сценарий. */
    private final BufferedReader in;
    /** Куда печатается диалог. */
    private final PrintStream out;
    /** Печатать ли прочитанные строки. */
    private final boolean echoInput;

    /**
     * Создаёт консоль.
     *
     * @param input  источник строк
     * @param output куда печатать
     * @param echo   печатать ли прочитанные строки (для демо)
     */
    public ConsoleIO(final Reader input, final PrintStream output,
                     final boolean echo) {
        this.in = new BufferedReader(Objects.requireNonNull(input, "input"));
        this.out = Objects.requireNonNull(output, "output");
        this.echoInput = echo;
    }

    /**
     * Печатает строку.
     *
     * @param text текст
     */
    public void println(final String text) {
        out.println(text);
    }

    /**
     * Печатает пустую строку.
     */
    public void println() {
        out.println();
    }

    /**
     * Читает непустую строку, переспрашивая при пустом вводе.
     *
     * @param prompt приглашение к вводу
     * @return строка без пробелов по краям
     * @throws InputClosedException если ввод закончился
     */
    public String readText(final String prompt) {
        while (true) {
            String line = readLine(prompt);
            if (!line.isBlank()) {
                return line.strip();
            }
            out.println("  Значение не может быть пустым.");
        }
    }

    /**
     * Читает целое число из диапазона [min, max], переспрашивая при ошибке.
     *
     * @param prompt приглашение к вводу
     * @param min    нижняя граница включительно
     * @param max    верхняя граница включительно
     * @return введённое число
     * @throws InputClosedException если ввод закончился
     */
    public int readInt(final String prompt, final int min, final int max) {
        while (true) {
            String line = readLine(prompt).strip();
            try {
                int value = Integer.parseInt(line);
                if (value >= min && value <= max) {
                    return value;
                }
            } catch (NumberFormatException ignored) {
                // ниже попросим ввести значение заново
            }
            out.printf("  Введите целое число от %d до %d.%n", min, max);
        }
    }

    /**
     * Читает положительное дробное число; принимает и точку, и запятую.
     *
     * @param prompt приглашение к вводу
     * @return введённое число
     * @throws InputClosedException если ввод закончился
     */
    public double readPositiveDouble(final String prompt) {
        while (true) {
            String line = readLine(prompt).strip().replace(',', '.');
            try {
                double value = Double.parseDouble(line);
                if (Double.isFinite(value) && value > 0) {
                    return value;
                }
            } catch (NumberFormatException ignored) {
                // ниже попросим ввести значение заново
            }
            out.println("  Введите положительное число, например 0.8");
        }
    }

    private String readLine(final String prompt) {
        out.print(prompt);
        out.flush();
        String line;
        try {
            line = in.readLine();
        } catch (IOException e) {
            throw new UncheckedIOException(e);
        }
        if (line == null) {
            out.println();
            throw new InputClosedException();
        }
        if (echoInput) {
            out.println(line);
        }
        return line;
    }
}
