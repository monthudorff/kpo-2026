package ru.hse.vyshkat.inspection;

import java.util.Locale;
import ru.hse.vyshkat.domain.Checks;
import ru.hse.vyshkat.domain.EnergyConsumer;
import ru.hse.vyshkat.domain.vehicle.Vehicle;

/**
 * Штатный техосмотр. Аномально высокий суточный расход энергии означает
 * неисправную батарею или контроллер — такой транспорт в парк не допускается.
 */
public final class StandardServiceCenter implements ServiceCenter {

    /** Норма суточного расхода для электротранспорта, кВт·ч. */
    private final double maxDailyEnergyKwh;

    /**
     * Создаёт техосмотр с заданной нормой расхода.
     *
     * @param maxEnergyKwh норма суточного расхода, кВт·ч
     */
    public StandardServiceCenter(final double maxEnergyKwh) {
        this.maxDailyEnergyKwh = Checks.requirePositive(
                maxEnergyKwh, "Норма расхода энергии");
    }

    @Override
    public InspectionResult inspect(final Vehicle vehicle) {
        if (vehicle instanceof EnergyConsumer consumer
                && consumer.getDailyEnergyKwh() > maxDailyEnergyKwh) {
            return InspectionResult.fail(String.format(Locale.ROOT,
                    "суточный расход %.2f кВт·ч выше нормы %.2f кВт·ч"
                            + " — неисправна батарея или контроллер",
                    consumer.getDailyEnergyKwh(), maxDailyEnergyKwh));
        }
        return InspectionResult.pass();
    }
}
