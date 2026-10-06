package ru.hse.vyshkat.domain.thing;

/**
 * Защитный шлем, выдаётся вместе с транспортом.
 */
public final class Helmet extends Thing {

    /** Название вида для отчётов и меню. */
    public static final String KIND = "Шлем";

    /**
     * Создаёт шлем.
     *
     * @param name            наименование
     * @param inventoryNumber инвентарный номер
     */
    public Helmet(final String name, final int inventoryNumber) {
        super(name, inventoryNumber);
    }

    @Override
    public String getKind() {
        return KIND;
    }
}
