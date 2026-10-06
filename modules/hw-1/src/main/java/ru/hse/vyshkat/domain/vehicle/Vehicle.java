package ru.hse.vyshkat.domain.vehicle;

import ru.hse.vyshkat.domain.Asset;
import ru.hse.vyshkat.domain.Checks;

/**
 * Прокатный транспорт. В парк попадает только через техосмотр.
 */
public abstract class Vehicle extends Asset {

    /** Минимальная оценка простоты для новичка. */
    public static final int MIN_SIMPLICITY = 1;
    /** Максимальная оценка простоты для новичка. */
    public static final int MAX_SIMPLICITY = 10;

    /** Простота для новичка, от 1 до 10. */
    private final int beginnerSimplicity;

    /**
     * Создаёт транспорт, проверяя оценку простоты.
     *
     * @param name            модель
     * @param inventoryNumber инвентарный номер
     * @param simplicity      простота для новичка, от 1 до 10
     */
    protected Vehicle(final String name, final int inventoryNumber,
                      final int simplicity) {
        super(name, inventoryNumber);
        this.beginnerSimplicity = Checks.requireInRange(simplicity,
                MIN_SIMPLICITY, MAX_SIMPLICITY, "Простота для новичка");
    }

    /**
     * Возвращает, насколько транспорт прост для новичка.
     *
     * @return оценка от 1 (сложно) до 10 (элементарно)
     */
    public final int getBeginnerSimplicity() {
        return beginnerSimplicity;
    }
}
