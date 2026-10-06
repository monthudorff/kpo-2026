package ru.hse.vyshkat.domain.vehicle;

/**
 * Электровелосипед.
 */
public final class ElectricBike extends ElectricVehicle {

    /** Название вида для отчётов и меню. */
    public static final String KIND = "Электровелосипед";

    /**
     * Создаёт электровелосипед.
     *
     * @param name            модель
     * @param inventoryNumber инвентарный номер
     * @param simplicity      простота для новичка, от 1 до 10
     * @param energyKwh       суточный расход энергии, кВт·ч
     */
    public ElectricBike(final String name, final int inventoryNumber,
                        final int simplicity, final double energyKwh) {
        super(name, inventoryNumber, simplicity, energyKwh);
    }

    @Override
    public String getKind() {
        return KIND;
    }
}
