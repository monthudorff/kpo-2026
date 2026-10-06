package ru.hse.vyshkat.fleet;

import ru.hse.vyshkat.domain.vehicle.Vehicle;

/**
 * Правило допуска транспорта к выдаче новичкам. Заказчик планирует его
 * усложнять, поэтому правило вынесено из отчётов в отдельную абстракцию.
 */
public interface BeginnerPolicy {

    /**
     * Решает, можно ли выдавать транспорт новичку.
     *
     * @param vehicle транспорт на балансе
     * @return {@code true}, если транспорт подходит новичку
     */
    boolean isSuitableForBeginner(Vehicle vehicle);

    /**
     * Возвращает формулировку правила для отчётов.
     *
     * @return человекочитаемое правило, например «простота ≥ 6»
     */
    String description();
}
