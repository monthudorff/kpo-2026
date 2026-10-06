package ru.hse.vyshkat.console;

import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.io.StringReader;
import java.nio.charset.StandardCharsets;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import ru.hse.vyshkat.fleet.AcceptanceService;
import ru.hse.vyshkat.fleet.AssetRegistry;
import ru.hse.vyshkat.fleet.FleetReportService;
import ru.hse.vyshkat.fleet.SimplicityThresholdPolicy;
import ru.hse.vyshkat.inspection.StandardServiceCenter;

/** Сценарии консольного меню: ввод подаётся строкой, проверяется вывод. */
@DisplayName("Консольное меню")
class ConsoleAppTest {

    private static final double NORM_KWH = 1.5;

    private static String run(final String... inputLines) {
        final AssetRegistry registry = new AssetRegistry();
        final AcceptanceService acceptance = new AcceptanceService(
                new StandardServiceCenter(NORM_KWH), registry);
        final FleetReportService reports = new FleetReportService(
                registry, new SimplicityThresholdPolicy(
                        SimplicityThresholdPolicy.DEFAULT_MIN_SIMPLICITY));

        final ByteArrayOutputStream buffer = new ByteArrayOutputStream();
        final PrintStream out =
                new PrintStream(buffer, true, StandardCharsets.UTF_8);
        final String input = String.join("\n", inputLines) + "\n";
        final ConsoleIO io =
                new ConsoleIO(new StringReader(input), out, false);

        new ConsoleApp(acceptance, reports, io).run();
        return buffer.toString(StandardCharsets.UTF_8);
    }

    private static void assertContains(final String output,
                                       final String... fragments) {
        for (String fragment : fragments) {
            assertTrue(output.contains(fragment),
                    "нет «" + fragment + "» в выводе:\n" + output);
        }
    }

    @Test
    @DisplayName("Принятый самокат виден во всех отчётах")
    void acceptedScooterAppearsInAllReports() {
        final String output = run(
                "1", "1", "Ninebot Max G30", "1001", "8", "0,6",
                "3", "4", "5", "6", "0");

        assertContains(output,
                "ПРИНЯТ: Электросамокат «Ninebot Max G30», инв. № 1001",
                "Всего: 1 (транспорт: 1, вещи: 0)",
                "Итого:        0.60 кВт·ч",
                "Электросамокат «Ninebot Max G30», инв. № 1001, простота 8",
                "№ 1001",
                "До встречи!");
    }

    @Test
    @DisplayName("Самокат с аномальным расходом отклонён техосмотром")
    void vehicleWithAbnormalConsumptionIsRejected() {
        final String output = run(
                "1", "1", "Kugoo Kirin M4", "1004", "7", "3.2", "6", "0");

        assertContains(output,
                "ОТКЛОНЁН: Электросамокат «Kugoo Kirin M4»",
                "выше нормы",
                "Баланс пуст.");
    }

    @Test
    @DisplayName("У велосипеда не спрашивают расход, он подходит новичкам")
    void bicycleIsNotAskedForEnergyAndCanBeForBeginners() {
        final String output = run("1", "2", "Stels", "1003", "9", "5", "0");

        assertContains(output,
                "ПРИНЯТ: Велосипед «Stels»",
                "Велосипед «Stels», инв. № 1003, простота 9");
    }

    @Test
    @DisplayName("Вещи попадают в ведомость и в отчёт по энергии")
    void thingsAreAddedToInventoryAndEnergyReport() {
        final String output = run(
                "2", "1", "Cairn Prism", "2001",
                "2", "3", "Склад на Мясницкой", "2003", "4.5",
                "2", "2", "Покровка", "2002", "1.2",
                "4", "6", "0");

        assertContains(output,
                "НА БАЛАНСЕ: Шлем «Cairn Prism», инв. № 2001",
                "Оборудование: 5.70 кВт·ч",
                "№ 2001   Шлем",
                "№ 2002   Док-станция",
                "№ 2003   Зарядный шкаф");
    }

    @Test
    @DisplayName("Занятый инвентарный номер — понятный отказ")
    void duplicateInventoryNumberIsReported() {
        final String output = run(
                "2", "1", "Cairn Prism", "7",
                "1", "2", "Stels", "7", "9",
                "0");

        assertContains(output, "ОТКАЗ: инвентарный номер 7 уже занят.");
    }

    @Test
    @DisplayName("Некорректный ввод запрашивается повторно")
    void invalidInputIsRequestedAgain() {
        final String output = run(
                "abc", "42",
                "1", "4", "1",
                "  ", "Ninebot",
                "-3", "1001",
                "11", "8",
                "ноль", "-1", "0.5",
                "5", "0");

        assertContains(output,
                "Введите целое число от 0 до 6.",
                "Введите целое число от 1 до 3.",
                "Значение не может быть пустым.",
                "Введите целое число от 1 до 10.",
                "Введите положительное число",
                "ПРИНЯТ: Электросамокат «Ninebot»");
    }

    @Test
    @DisplayName("Отчёты по пустому парку читаются без ошибок")
    void emptyFleetReportsAreReadable() {
        final String output = run("3", "4", "5", "6", "0");

        assertContains(output,
                "Всего: 0",
                "Итого:        0.00 кВт·ч",
                "Подходящего транспорта нет.",
                "Баланс пуст.");
    }

    @Test
    @DisplayName("Конец ввода завершает программу корректно")
    void endOfInputFinishesGracefully() {
        final String output = run("1", "1", "Ninebot");

        assertContains(output, "Ввод завершён.", "До встречи!");
    }
}
