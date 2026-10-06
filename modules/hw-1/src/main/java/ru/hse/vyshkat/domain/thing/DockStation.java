package ru.hse.vyshkat.domain.thing;

import ru.hse.vyshkat.domain.Checks;
import ru.hse.vyshkat.domain.EnergyConsumer;

/**
 * Док-станция для парковки и подзарядки транспорта.
 */
public final class DockStation extends Thing implements EnergyConsumer {

    /** Название вида для отчётов и меню. */
    public static final String KIND = "Док-станция";

    /** Суточный расход энергии, кВт·ч. */
    private final double dailyEnergyKwh;

    /**
     * Создаёт док-станцию.
     *
     * @param name            адрес или наименование
     * @param inventoryNumber инвентарный номер
     * @param energyKwh       суточный расход энергии, кВт·ч
     */
    public DockStation(final String name, final int inventoryNumber,
                       final double energyKwh) {
        super(name, inventoryNumber);
        this.dailyEnergyKwh = Checks.requirePositive(
                energyKwh, "Суточный расход энергии");
    }

    @Override
    public double getDailyEnergyKwh() {
        return dailyEnergyKwh;
    }

    @Override
    public String getKind() {
        return KIND;
    }
}
