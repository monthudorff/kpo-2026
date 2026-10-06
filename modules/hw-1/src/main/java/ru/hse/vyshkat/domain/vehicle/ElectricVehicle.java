package ru.hse.vyshkat.domain.vehicle;

import ru.hse.vyshkat.domain.Checks;
import ru.hse.vyshkat.domain.EnergyConsumer;

/**
 * Транспорт с электроприводом: хранит суточный расход энергии.
 */
public abstract class ElectricVehicle extends Vehicle
        implements EnergyConsumer {

    /** Суточный расход энергии, кВт·ч. */
    private final double dailyEnergyKwh;

    /**
     * Создаёт электротранспорт, проверяя расход энергии.
     *
     * @param name            модель
     * @param inventoryNumber инвентарный номер
     * @param simplicity      простота для новичка, от 1 до 10
     * @param energyKwh       суточный расход энергии, кВт·ч
     */
    protected ElectricVehicle(final String name, final int inventoryNumber,
                              final int simplicity, final double energyKwh) {
        super(name, inventoryNumber, simplicity);
        this.dailyEnergyKwh = Checks.requirePositive(
                energyKwh, "Суточный расход энергии");
    }

    @Override
    public final double getDailyEnergyKwh() {
        return dailyEnergyKwh;
    }
}
