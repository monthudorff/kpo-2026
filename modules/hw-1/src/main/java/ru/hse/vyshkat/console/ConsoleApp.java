package ru.hse.vyshkat.console;

import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;
import java.util.function.Function;
import ru.hse.vyshkat.domain.Asset;
import ru.hse.vyshkat.domain.thing.Thing;
import ru.hse.vyshkat.domain.vehicle.Vehicle;
import ru.hse.vyshkat.fleet.AcceptanceService;
import ru.hse.vyshkat.fleet.DuplicateInventoryNumberException;
import ru.hse.vyshkat.fleet.FleetReportService;
import ru.hse.vyshkat.fleet.FleetReportService.EnergyReport;
import ru.hse.vyshkat.fleet.FleetReportService.FleetSummary;
import ru.hse.vyshkat.inspection.InspectionResult;

/**
 * Консольное меню оператора: приём техники и вещей, отчёты по балансу.
 * Бизнес-логики здесь нет — только диалог и форматирование.
 */
public final class ConsoleApp {

    /** Пункт меню «Выход». */
    private static final int EXIT = 0;
    /** Пункт меню «Принять транспорт». */
    private static final int ACCEPT_VEHICLE = 1;
    /** Пункт меню «Поставить вещь на баланс». */
    private static final int ACCEPT_THING = 2;
    /** Пункт меню «Количество единиц». */
    private static final int SUMMARY = 3;
    /** Пункт меню «Энергопотребление». */
    private static final int ENERGY = 4;
    /** Пункт меню «Транспорт для новичков». */
    private static final int BEGINNERS = 5;
    /** Пункт меню «Инвентарная ведомость». */
    private static final int INVENTORY = 6;

    /** Текст меню; номера совпадают с константами выше. */
    private static final String MENU = """
            ===== ВышКат: учёт парка =====
            1. Принять транспорт (через техосмотр)
            2. Поставить вещь на баланс
            3. Отчёт: количество единиц
            4. Отчёт: суточное энергопотребление
            5. Транспорт для новичков
            6. Инвентарная ведомость
            0. Выход""";

    /** Приёмка техники и вещей. */
    private final AcceptanceService acceptanceService;
    /** Отчёты по балансу. */
    private final FleetReportService reportService;
    /** Ввод-вывод. */
    private final ConsoleIO io;

    /**
     * Создаёт меню оператора.
     *
     * @param acceptance приёмка
     * @param reports    отчёты
     * @param consoleIO  ввод-вывод
     */
    public ConsoleApp(final AcceptanceService acceptance,
                      final FleetReportService reports,
                      final ConsoleIO consoleIO) {
        this.acceptanceService =
                Objects.requireNonNull(acceptance, "acceptance");
        this.reportService = Objects.requireNonNull(reports, "reports");
        this.io = Objects.requireNonNull(consoleIO, "consoleIO");
    }

    /**
     * Крутит меню, пока оператор не выберет «Выход» или не закончится ввод.
     */
    public void run() {
        io.println("«ВышКат» — доедем до дедлайна!");
        try {
            int choice = askMenuChoice();
            while (choice != EXIT) {
                io.println();
                handle(choice);
                io.println();
                choice = askMenuChoice();
            }
        } catch (InputClosedException e) {
            io.println("Ввод завершён.");
        }
        io.println("До встречи!");
    }

    private int askMenuChoice() {
        io.println(MENU);
        return io.readInt("Выберите пункт: ", EXIT, INVENTORY);
    }

    private void handle(final int choice) {
        switch (choice) {
            case ACCEPT_VEHICLE -> acceptVehicle();
            case ACCEPT_THING -> acceptThing();
            case SUMMARY -> printSummary();
            case ENERGY -> printEnergyReport();
            case BEGINNERS -> printBeginnerVehicles();
            case INVENTORY -> printInventory();
            default -> throw new IllegalArgumentException(
                    "Нет пункта меню " + choice);
        }
    }

    private void acceptVehicle() {
        io.println("--- Приёмка транспорта ---");
        VehicleKind kind = choose("Вид транспорта", VehicleKind.values(),
                VehicleKind::title);
        String name = io.readText("Модель: ");
        int number = readInventoryNumber();
        int simplicity = io.readInt("Простота для новичка (1-10): ",
                Vehicle.MIN_SIMPLICITY, Vehicle.MAX_SIMPLICITY);
        double kwh = kind.consumesEnergy() ? readDailyEnergy() : 0;
        try {
            Vehicle vehicle = kind.create(name, number, simplicity, kwh);
            InspectionResult result = acceptanceService.acceptVehicle(vehicle);
            String verdict = result.passed() ? "ПРИНЯТ" : "ОТКЛОНЁН";
            io.println(verdict + ": " + vehicle + " — "
                    + result.reason() + ".");
        } catch (DuplicateInventoryNumberException
                | IllegalArgumentException e) {
            io.println("ОТКАЗ: " + e.getMessage() + ".");
        }
    }

    private void acceptThing() {
        io.println("--- Постановка вещи на баланс ---");
        ThingKind kind = choose("Вид вещи", ThingKind.values(),
                ThingKind::title);
        String name = io.readText("Наименование: ");
        int number = readInventoryNumber();
        double kwh = kind.consumesEnergy() ? readDailyEnergy() : 0;
        try {
            Thing thing = kind.create(name, number, kwh);
            acceptanceService.acceptThing(thing);
            io.println("НА БАЛАНСЕ: " + thing + ".");
        } catch (DuplicateInventoryNumberException
                | IllegalArgumentException e) {
            io.println("ОТКАЗ: " + e.getMessage() + ".");
        }
    }

    private void printSummary() {
        io.println("--- Количество единиц на балансе ---");
        FleetSummary summary = reportService.summary();
        io.println("Всего: %d (транспорт: %d, вещи: %d)".formatted(
                summary.total(), summary.vehicles(), summary.things()));
        Map<String, Long> byKind = summary.countByKind();
        for (Map.Entry<String, Long> entry : byKind.entrySet()) {
            io.println("  %s: %d".formatted(entry.getKey(), entry.getValue()));
        }
    }

    private void printEnergyReport() {
        io.println("--- Суточное энергопотребление ---");
        EnergyReport report = reportService.energyReport();
        io.println("Транспорт:    " + formatKwh(report.vehiclesKwh()));
        io.println("Оборудование: " + formatKwh(report.thingsKwh()));
        io.println("Итого:        " + formatKwh(report.totalKwh()));
    }

    private void printBeginnerVehicles() {
        io.println("--- Транспорт для новичков ("
                + reportService.beginnerRule() + ") ---");
        List<Vehicle> vehicles = reportService.beginnerFriendlyVehicles();
        if (vehicles.isEmpty()) {
            io.println("  Подходящего транспорта нет.");
        }
        for (Vehicle vehicle : vehicles) {
            io.println("  " + vehicle + ", простота "
                    + vehicle.getBeginnerSimplicity());
        }
    }

    private void printInventory() {
        io.println("--- Инвентарная ведомость ---");
        List<Asset> assets = reportService.inventoryList();
        if (assets.isEmpty()) {
            io.println("  Баланс пуст.");
        }
        for (Asset asset : assets) {
            io.println("  № %-6d %-17s %s".formatted(
                    asset.getInventoryNumber(), asset.getKind(),
                    asset.getName()));
        }
    }

    private <E> E choose(final String header, final E[] options,
                         final Function<E, String> title) {
        io.println(header + ":");
        for (int i = 0; i < options.length; i++) {
            io.println("  %d. %s".formatted(i + 1, title.apply(options[i])));
        }
        return options[io.readInt("Ваш выбор: ", 1, options.length) - 1];
    }

    private int readInventoryNumber() {
        return io.readInt("Инвентарный номер: ", 1, Integer.MAX_VALUE);
    }

    private double readDailyEnergy() {
        return io.readPositiveDouble("Суточный расход энергии, кВт·ч: ");
    }

    private static String formatKwh(final double kwh) {
        return String.format(Locale.ROOT, "%.2f кВт·ч", kwh);
    }
}
