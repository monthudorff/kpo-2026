package ru.hse.vyshkat;

import java.io.Console;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.PrintStream;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.Objects;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import ru.hse.vyshkat.config.ConsoleConfig;
import ru.hse.vyshkat.console.ConsoleApp;
import ru.hse.vyshkat.console.ConsoleIO;

/**
 * Точка входа. Без аргументов — интерактивное меню, с {@code --demo} —
 * прогон демо-сценария из {@code demo-script.txt} со всеми ветками.
 */
public final class Main {

    /** Аргумент запуска демо-сценария. */
    static final String DEMO_FLAG = "--demo";
    /** Демо-сценарий в ресурсах. */
    static final String DEMO_SCRIPT = "/demo-script.txt";

    private Main() {
    }

    /**
     * Собирает DI-контейнер и запускает меню.
     *
     * @param args {@code --demo} для демо-сценария
     */
    public static void main(final String[] args) {
        boolean demo = Arrays.asList(args).contains(DEMO_FLAG);
        ConsoleIO io = demo ? demoIO(System.out) : interactiveIO(System.out);

        try (var context = new AnnotationConfigApplicationContext()) {
            context.registerBean(ConsoleIO.class, () -> io);
            context.register(ConsoleConfig.class);
            context.refresh();
            context.getBean(ConsoleApp.class).run();
        }
    }

    private static ConsoleIO interactiveIO(final PrintStream out) {
        Console console = System.console();
        Charset charset = console != null
                ? console.charset()
                : Charset.defaultCharset();
        return new ConsoleIO(
                new InputStreamReader(System.in, charset), out, false);
    }

    private static ConsoleIO demoIO(final PrintStream out) {
        InputStream script = Objects.requireNonNull(
                Main.class.getResourceAsStream(DEMO_SCRIPT),
                "Не найден демо-сценарий " + DEMO_SCRIPT);
        return new ConsoleIO(
                new InputStreamReader(script, StandardCharsets.UTF_8),
                out, true);
    }
}
