package ru.hse.vyshkat.domain.thing;

import ru.hse.vyshkat.domain.Asset;

/**
 * Вещь на балансе (не сдаётся в прокат). Ставится на баланс без техосмотра.
 */
public abstract class Thing extends Asset {

    /**
     * Создаёт вещь.
     *
     * @param name            наименование
     * @param inventoryNumber инвентарный номер
     */
    protected Thing(final String name, final int inventoryNumber) {
        super(name, inventoryNumber);
    }
}
