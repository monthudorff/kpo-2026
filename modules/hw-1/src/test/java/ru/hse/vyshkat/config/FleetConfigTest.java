package ru.hse.vyshkat.config;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertInstanceOf;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.junit.jupiter.SpringJUnitConfig;
import ru.hse.vyshkat.domain.vehicle.ElectricScooter;
import ru.hse.vyshkat.domain.vehicle.Vehicle;
import ru.hse.vyshkat.fleet.AcceptanceService;
import ru.hse.vyshkat.fleet.FleetReportService;
import ru.hse.vyshkat.inspection.ServiceCenter;
import ru.hse.vyshkat.inspection.StandardServiceCenter;

/**
 * Интеграционные тесты боевой конфигурации контейнера: штатный техосмотр
 * и общий баланс у приёмки и отчётов.
 */
@SpringJUnitConfig(FleetConfig.class)
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
@DisplayName("Боевая DI-конфигурация")
class FleetConfigTest {

    private static final int SIMPLICITY = 8;
    private static final double NORMAL_KWH = 0.6;

    @Autowired
    private ServiceCenter serviceCenter;

    @Autowired
    private AcceptanceService acceptanceService;

    @Autowired
    private FleetReportService reportService;

    @Test
    @DisplayName("Контейнер внедряет штатный техосмотр")
    void containerProvidesStandardServiceCenter() {
        assertInstanceOf(StandardServiceCenter.class, serviceCenter);
    }

    @Test
    @DisplayName("Принятый транспорт виден в отчётах: баланс общий")
    void acceptedVehicleIsVisibleInReportsBecauseRegistryIsShared() {
        final Vehicle scooter =
                new ElectricScooter("Ninebot", 1, SIMPLICITY, NORMAL_KWH);

        assertTrue(acceptanceService.acceptVehicle(scooter).passed());
        assertEquals(1, reportService.summary().vehicles());
        assertEquals(1, reportService.beginnerFriendlyVehicles().size());
    }

    @Test
    @DisplayName("Штатный техосмотр отклоняет расход выше нормы")
    void standardInspectionRejectsVehicleAboveEnergyNorm() {
        final double aboveNorm = FleetConfig.MAX_VEHICLE_DAILY_ENERGY_KWH + 1;
        final Vehicle faulty =
                new ElectricScooter("Kugoo", 1, SIMPLICITY, aboveNorm);

        assertFalse(acceptanceService.acceptVehicle(faulty).passed());
        assertEquals(0, reportService.summary().total());
    }
}
