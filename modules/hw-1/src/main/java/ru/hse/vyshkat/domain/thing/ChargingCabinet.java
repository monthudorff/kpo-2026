package ru.hse.vyshkat.domain.thing;

import ru.hse.vyshkat.domain.Checks;
import ru.hse.vyshkat.domain.EnergyConsumer;

/**
 * Зарядный шкаф для съёмных аккумуляторов.
 */
public final class ChargingCabinet extends Thing implements EnergyConsumer {

    /** Название вида для отчётов и меню. */
    public static final String KIND = "Зарядный шкаф";

    /** Суточный расход энергии, кВт·ч. */
    private final double dailyEnergyKwh;

    /**
     * Создаёт зарядный шкаф.
     *
     * @param name            наименование
     * @param inventoryNumber инвентарный номер
     * @param energyKwh       суточный расход энергии, кВт·ч
     */
    public ChargingCabinet(final String name, final int inventoryNumber,
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
