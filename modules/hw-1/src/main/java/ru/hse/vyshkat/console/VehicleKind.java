package ru.hse.vyshkat.console;

import ru.hse.vyshkat.domain.vehicle.Bicycle;
import ru.hse.vyshkat.domain.vehicle.ElectricBike;
import ru.hse.vyshkat.domain.vehicle.ElectricScooter;
import ru.hse.vyshkat.domain.vehicle.Vehicle;

/**
 * Каталог видов транспорта для меню приёмки.
 * Новый вид — это новый класс в домене и одна строка здесь.
 */
enum VehicleKind {

    /** Электросамокат. */
    ELECTRIC_SCOOTER(ElectricScooter.KIND, true, ElectricScooter::new),
    /** Велосипед: расход энергии не спрашивается. */
    BICYCLE(Bicycle.KIND, false,
            (name, number, simplicity, kwh) ->
                    new Bicycle(name, number, simplicity)),
    /** Электровелосипед. */
    ELECTRIC_BIKE(ElectricBike.KIND, true, ElectricBike::new);

    /** Название пункта меню. */
    private final String title;
    /** Нужно ли спрашивать расход энергии. */
    private final boolean consumesEnergy;
    /** Как создать объект этого вида. */
    private final Factory factory;

    VehicleKind(final String kindTitle, final boolean electric,
                final Factory vehicleFactory) {
        this.title = kindTitle;
        this.consumesEnergy = electric;
        this.factory = vehicleFactory;
    }

    String title() {
        return title;
    }

    boolean consumesEnergy() {
        return consumesEnergy;
    }

    Vehicle create(final String name, final int inventoryNumber,
                   final int simplicity, final double dailyEnergyKwh) {
        return factory.create(name, inventoryNumber, simplicity,
                dailyEnergyKwh);
    }

    /** Конструктор транспорта с параметрами из меню. */
    @FunctionalInterface
    private interface Factory {
        Vehicle create(String name, int inventoryNumber, int simplicity,
                       double dailyEnergyKwh);
    }
}
