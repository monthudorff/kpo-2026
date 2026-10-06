package ru.hse.vyshkat.domain.vehicle;

/**
 * Обычный велосипед — энергию не потребляет.
 */
public final class Bicycle extends Vehicle {

    /** Название вида для отчётов и меню. */
    public static final String KIND = "Велосипед";

    /**
     * Создаёт велосипед.
     *
     * @param name            модель
     * @param inventoryNumber инвентарный номер
     * @param simplicity      простота для новичка, от 1 до 10
     */
    public Bicycle(final String name, final int inventoryNumber,
                   final int simplicity) {
        super(name, inventoryNumber, simplicity);
    }

    @Override
    public String getKind() {
        return KIND;
    }
}
