package ru.hse.vyshkat.inspection;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import ru.hse.vyshkat.domain.vehicle.Bicycle;
import ru.hse.vyshkat.domain.vehicle.ElectricBike;
import ru.hse.vyshkat.domain.vehicle.ElectricScooter;

/** Unit-тесты штатного техосмотра. */
@DisplayName("Штатный техосмотр")
class StandardServiceCenterTest {

    private static final double NORM_KWH = 1.5;
    private static final double NORMAL_KWH = 0.6;
    private static final double FAULTY_KWH = 3.2;
    private static final int SIMPLICITY = 7;

    private final ServiceCenter serviceCenter =
            new StandardServiceCenter(NORM_KWH);

    @Test
    @DisplayName("Велосипед без мотора проходит техосмотр")
    void bicycleWithoutMotorPassesInspection() {
        assertTrue(serviceCenter.inspect(
                new Bicycle("Stels", 1, SIMPLICITY)).passed());
    }

    @Test
    @DisplayName("Расход в пределах нормы, включая границу, — принят")
    void electricVehicleWithinNormPasses() {
        assertTrue(serviceCenter.inspect(new ElectricScooter(
                "Ninebot", 1, SIMPLICITY, NORMAL_KWH)).passed());
        assertTrue(serviceCenter.inspect(new ElectricBike(
                "Eltreco", 2, SIMPLICITY, NORM_KWH)).passed());
    }

    @Test
    @DisplayName("Расход выше нормы — отказ с причиной")
    void electricVehicleAboveNormIsRejectedWithReason() {
        final InspectionResult result = serviceCenter.inspect(
                new ElectricScooter("Kugoo", 1, SIMPLICITY, FAULTY_KWH));

        assertFalse(result.passed());
        assertTrue(result.reason().contains("3.20"), result.reason());
        assertTrue(result.reason().contains("выше нормы"), result.reason());
    }

    @Test
    @DisplayName("Норма расхода должна быть положительной")
    void normMustBePositive() {
        assertThrows(IllegalArgumentException.class,
                () -> new StandardServiceCenter(0));
    }

    @Test
    @DisplayName("Заключение техосмотра требует пояснения")
    void inspectionResultRequiresReason() {
        assertThrows(IllegalArgumentException.class,
                () -> InspectionResult.fail(" "));
    }
}
