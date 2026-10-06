package ru.hse.vyshkat.inspection;

import ru.hse.vyshkat.domain.Checks;

/**
 * Заключение техосмотра: принять устройство в парк или отклонить.
 *
 * @param passed {@code true}, если техосмотр пройден
 * @param reason пояснение для оператора, в том числе причина отказа
 */
public record InspectionResult(boolean passed, String reason) {

    /**
     * Проверяет, что у заключения есть пояснение.
     */
    public InspectionResult {
        reason = Checks.requireNotBlank(reason, "Причина");
    }

    /**
     * Создаёт положительное заключение.
     *
     * @return техосмотр пройден
     */
    public static InspectionResult pass() {
        return new InspectionResult(true, "техосмотр пройден");
    }

    /**
     * Создаёт отказ с причиной.
     *
     * @param cause причина отказа
     * @return техосмотр не пройден
     */
    public static InspectionResult fail(final String cause) {
        return new InspectionResult(false, cause);
    }
}
