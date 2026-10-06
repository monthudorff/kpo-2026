package ru.hse.vyshkat.fleet;

/**
 * На баланс пытаются поставить объект с уже занятым инвентарным номером.
 */
public final class DuplicateInventoryNumberException extends RuntimeException {

    /**
     * Создаёт исключение для занятого номера.
     *
     * @param inventoryNumber занятый номер
     */
    public DuplicateInventoryNumberException(final int inventoryNumber) {
        super("инвентарный номер " + inventoryNumber + " уже занят");
    }
}
