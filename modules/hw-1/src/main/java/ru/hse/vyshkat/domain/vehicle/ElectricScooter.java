package ru.hse.vyshkat.domain.vehicle;

/**
 * Электросамокат — основа кикшеринга.
 */
public final class ElectricScooter extends ElectricVehicle {

    /** Название вида для отчётов и меню. */
    public static final String KIND = "Электросамокат";

    /**
     * Создаёт электросамокат.
     *
     * @param name            модель
     * @param inventoryNumber инвентарный номер
     * @param simplicity      простота для новичка, от 1 до 10
     * @param energyKwh       суточный расход энергии, кВт·ч
     */
    public ElectricScooter(final String name, final int inventoryNumber,
                           final int simplicity, final double energyKwh) {
        super(name, inventoryNumber, simplicity, energyKwh);
    }

    @Override
    public String getKind() {
        return KIND;
    }
}
