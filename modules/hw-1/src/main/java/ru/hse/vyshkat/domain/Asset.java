package ru.hse.vyshkat.domain;

/**
 * Всё, что ставится на баланс: транспорт и вещи. Сотрудники сюда не входят.
 */
public abstract class Asset implements Inventoried {

    /** Наименование для инвентарной ведомости. */
    private final String name;
    /** Инвентарный номер, уникальный в пределах баланса. */
    private final int inventoryNumber;

    /**
     * Создаёт объект учёта, проверяя наименование и номер.
     *
     * @param assetName наименование, не пустое
     * @param number    инвентарный номер, положительный
     */
    protected Asset(final String assetName, final int number) {
        this.name = Checks.requireNotBlank(assetName, "Наименование");
        this.inventoryNumber = Checks.requirePositive(
                number, "Инвентарный номер");
    }

    @Override
    public final String getName() {
        return name;
    }

    @Override
    public final int getInventoryNumber() {
        return inventoryNumber;
    }

    /**
     * Возвращает вид единицы для отчётов, например «Велосипед».
     *
     * @return название вида
     */
    public abstract String getKind();

    @Override
    public final String toString() {
        return "%s «%s», инв. № %d"
                .formatted(getKind(), name, inventoryNumber);
    }
}
