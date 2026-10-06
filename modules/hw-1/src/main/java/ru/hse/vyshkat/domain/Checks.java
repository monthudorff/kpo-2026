package ru.hse.vyshkat.domain;

/**
 * Проверки инвариантов доменных объектов.
 * Нарушение — {@link IllegalArgumentException}.
 */
public final class Checks {

    private Checks() {
    }

    /**
     * Проверяет, что строка не пустая, и обрезает пробелы по краям.
     *
     * @param value проверяемое значение
     * @param field название поля для текста ошибки
     * @return значение без пробелов по краям
     */
    public static String requireNotBlank(final String value,
                                         final String field) {
        if (value == null || value.isBlank()) {
            throw new IllegalArgumentException(
                    field + ": значение не может быть пустым");
        }
        return value.strip();
    }

    /**
     * Проверяет, что целое число положительное.
     *
     * @param value проверяемое значение
     * @param field название поля для текста ошибки
     * @return то же значение
     */
    public static int requirePositive(final int value, final String field) {
        if (value <= 0) {
            throw new IllegalArgumentException(
                    field + ": нужно положительное число, получено " + value);
        }
        return value;
    }

    /**
     * Проверяет, что дробное число конечное и положительное.
     *
     * @param value проверяемое значение
     * @param field название поля для текста ошибки
     * @return то же значение
     */
    public static double requirePositive(final double value,
                                         final String field) {
        if (!Double.isFinite(value) || value <= 0) {
            throw new IllegalArgumentException(
                    field + ": нужно положительное число, получено " + value);
        }
        return value;
    }

    /**
     * Проверяет, что целое число лежит в диапазоне [min, max].
     *
     * @param value проверяемое значение
     * @param min   нижняя граница включительно
     * @param max   верхняя граница включительно
     * @param field название поля для текста ошибки
     * @return то же значение
     */
    public static int requireInRange(final int value, final int min,
                                     final int max, final String field) {
        if (value < min || value > max) {
            throw new IllegalArgumentException(
                    "%s: нужно число от %d до %d, получено %d"
                            .formatted(field, min, max, value));
        }
        return value;
    }
}
