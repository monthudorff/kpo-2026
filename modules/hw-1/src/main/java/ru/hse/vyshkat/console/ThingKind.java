package ru.hse.vyshkat.console;

import ru.hse.vyshkat.domain.thing.ChargingCabinet;
import ru.hse.vyshkat.domain.thing.DockStation;
import ru.hse.vyshkat.domain.thing.Helmet;
import ru.hse.vyshkat.domain.thing.Thing;

/**
 * Каталог видов вещей для меню постановки на баланс.
 */
enum ThingKind {

    /** Шлем: расход энергии не спрашивается. */
    HELMET(Helmet.KIND, false,
            (name, number, kwh) -> new Helmet(name, number)),
    /** Док-станция. */
    DOCK_STATION(DockStation.KIND, true, DockStation::new),
    /** Зарядный шкаф. */
    CHARGING_CABINET(ChargingCabinet.KIND, true, ChargingCabinet::new);

    /** Название пункта меню. */
    private final String title;
    /** Нужно ли спрашивать расход энергии. */
    private final boolean consumesEnergy;
    /** Как создать объект этого вида. */
    private final Factory factory;

    ThingKind(final String kindTitle, final boolean powered,
              final Factory thingFactory) {
        this.title = kindTitle;
        this.consumesEnergy = powered;
        this.factory = thingFactory;
    }

    String title() {
        return title;
    }

    boolean consumesEnergy() {
        return consumesEnergy;
    }

    Thing create(final String name, final int inventoryNumber,
                 final double dailyEnergyKwh) {
        return factory.create(name, inventoryNumber, dailyEnergyKwh);
    }

    /** Конструктор вещи с параметрами из меню. */
    @FunctionalInterface
    private interface Factory {
        Thing create(String name, int inventoryNumber, double dailyEnergyKwh);
    }
}
