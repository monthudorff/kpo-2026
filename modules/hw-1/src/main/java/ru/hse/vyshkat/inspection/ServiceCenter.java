package ru.hse.vyshkat.inspection;

import ru.hse.vyshkat.domain.vehicle.Vehicle;

/**
 * Сервис техосмотра: решает, можно ли принять транспорт в активный парк.
 */
public interface ServiceCenter {

    /**
     * Проводит техосмотр.
     *
     * @param vehicle транспорт, который хотят принять в парк
     * @return заключение: принять или отклонить с причиной
     */
    InspectionResult inspect(Vehicle vehicle);
}
