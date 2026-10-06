package ru.hse.vyshkat;

import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.io.PrintStream;
import java.nio.charset.StandardCharsets;
import java.util.List;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

/** Запуск через Main: контейнер собирается целиком, демо идёт до конца. */
@DisplayName("Запуск приложения")
class MainTest {

    private final ByteArrayOutputStream buffer = new ByteArrayOutputStream();
    private PrintStream originalOut;
    private InputStream originalIn;

    @BeforeEach
    void redirectStreams() {
        originalOut = System.out;
        originalIn = System.in;
        System.setOut(new PrintStream(buffer, true, StandardCharsets.UTF_8));
    }

    @AfterEach
    void restoreStreams() {
        System.setOut(originalOut);
        System.setIn(originalIn);
    }

    @Test
    @DisplayName("Демо-сценарий проходит все ветки приложения")
    void demoScenarioCoversAllFeatures() {
        final String[] demoArgs = {Main.DEMO_FLAG};

        Main.main(demoArgs);

        final String output = buffer.toString(StandardCharsets.UTF_8);
        final List<String> expected = List.of(
                "Баланс пуст.",
                "ПРИНЯТ: Электросамокат «Ninebot Max G30»",
                "ОТКЛОНЁН: Электросамокат «Kugoo Kirin M4»",
                "ОТКАЗ: инвентарный номер 1001 уже занят.",
                "НА БАЛАНСЕ: Зарядный шкаф",
                "Всего: 6 (транспорт: 3, вещи: 3)",
                "Итого:        7.20 кВт·ч",
                "Велосипед «Stels Navigator 300», инв. № 1003, простота 9",
                "До встречи!");
        for (String fragment : expected) {
            assertTrue(output.contains(fragment),
                    "нет «" + fragment + "» в выводе демо");
        }
    }

    @Test
    @DisplayName("Интерактивный режим читает стандартный ввод")
    void interactiveModeReadsStandardInput() {
        System.setIn(new ByteArrayInputStream(
                "6\n0\n".getBytes(StandardCharsets.UTF_8)));

        Main.main(new String[0]);

        final String output = buffer.toString(StandardCharsets.UTF_8);
        assertTrue(output.contains("Баланс пуст."), output);
        assertTrue(output.contains("До встречи!"), output);
    }
}
