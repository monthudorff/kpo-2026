package ru.hse.vyshkat.fleet;

import ru.hse.vyshkat.domain.Checks;
import ru.hse.vyshkat.domain.vehicle.Vehicle;

/**
 * Базовое правило: транспорт подходит новичку, если его «простота»
 * не ниже порога.
 */
public final class SimplicityThresholdPolicy implements BeginnerPolicy {

    /** Порог из требований заказчика. */
    public static final int DEFAULT_MIN_SIMPLICITY = 6;

    /** Минимальная простота, с которой транспорт выдают новичкам. */
    private final int minSimplicity;

    /**
     * Создаёт правило с заданным порогом.
     *
     * @param threshold минимальная простота, от 1 до 10
     */
    public SimplicityThresholdPolicy(final int threshold) {
        this.minSimplicity = Checks.requireInRange(threshold,
                Vehicle.MIN_SIMPLICITY, Vehicle.MAX_SIMPLICITY,
                "Порог простоты");
    }

    @Override
    public boolean isSuitableForBeginner(final Vehicle vehicle) {
        return vehicle.getBeginnerSimplicity() >= minSimplicity;
    }

    @Override
    public String description() {
        return "простота для новичка ≥ " + minSimplicity;
    }
}
