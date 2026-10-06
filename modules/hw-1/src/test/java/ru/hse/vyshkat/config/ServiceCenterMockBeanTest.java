package ru.hse.vyshkat.config;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.context.junit.jupiter.SpringJUnitConfig;
import ru.hse.vyshkat.domain.vehicle.ElectricScooter;
import ru.hse.vyshkat.domain.vehicle.Vehicle;
import ru.hse.vyshkat.fleet.AcceptanceService;
import ru.hse.vyshkat.fleet.FleetReportService;
import ru.hse.vyshkat.inspection.InspectionResult;
import ru.hse.vyshkat.inspection.ServiceCenter;

/**
 * Подмена техосмотра в DI-контейнере: {@link MockitoBean} заменяет бин
 * {@link ServiceCenter} моком, и контейнер сам внедряет мок в приёмку.
 */
@SpringJUnitConfig(FleetConfig.class)
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
@DisplayName("Подмена техосмотра через @MockitoBean")
class ServiceCenterMockBeanTest {

    private static final int SIMPLICITY = 8;
    private static final double NORMAL_KWH = 0.6;
    private static final double FAULTY_KWH = 3.2;

    @MockitoBean
    private ServiceCenter serviceCenter;

    @Autowired
    private AcceptanceService acceptanceService;

    @Autowired
    private FleetReportService reportService;

    @Test
    @DisplayName("Мок отклоняет исправный самокат — его нет на балансе")
    void containerInjectsMockedRejection() {
        when(serviceCenter.inspect(any()))
                .thenReturn(InspectionResult.fail("стенд на ремонте"));
        final Vehicle healthy =
                new ElectricScooter("Ninebot", 1, SIMPLICITY, NORMAL_KWH);

        final InspectionResult result =
                acceptanceService.acceptVehicle(healthy);

        assertFalse(result.passed());
        assertEquals(0, reportService.summary().total());
        verify(serviceCenter).inspect(healthy);
    }

    @Test
    @DisplayName("Мок пропускает неисправный самокат — он на балансе")
    void containerInjectsMockedApproval() {
        when(serviceCenter.inspect(any())).thenReturn(InspectionResult.pass());
        final Vehicle faulty =
                new ElectricScooter("Kugoo", 1, SIMPLICITY, FAULTY_KWH);

        assertTrue(acceptanceService.acceptVehicle(faulty).passed());
        assertEquals(1, reportService.summary().vehicles());
    }
}
