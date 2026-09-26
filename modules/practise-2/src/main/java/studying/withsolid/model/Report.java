package studying.withsolid.model;

import lombok.Builder;

import java.time.LocalDate;
import java.time.LocalTime;

/** Immutable data that describes one sales report. */
@Builder
public record Report(
        String title,
        LocalDate date,
        LocalTime time,
        int carsSold,
        int motorcyclesSold
) {
    /**
     * Produces the text-file representation of this report.
     *
     * @return formatted report content
     */
    @Override
    public String toString() {
        return "%s%nДата: %s%nВремя: %s%n--------------------------------%n"
                .formatted(title, date, time.withNano(0))
                + "Продано автомобилей: %d шт.%nПродано мотоциклов: %d шт.%n--------------------------------%n"
                .formatted(carsSold, motorcyclesSold);
    }
}
