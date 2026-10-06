package ru.hse.vyshkat.fleet;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

import java.util.List;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import ru.hse.vyshkat.domain.thing.Helmet;
import ru.hse.vyshkat.domain.vehicle.ElectricScooter;
import ru.hse.vyshkat.domain.vehicle.Vehicle;
import ru.hse.vyshkat.inspection.InspectionResult;
import ru.hse.vyshkat.inspection.ServiceCenter;

/**
 * Unit-тесты приёмки. Техосмотр подменяется через конструктор:
 * стабами-лямбдами и Mockito-моком вместо штатного техосмотра.
 */
@DisplayName("Приёмка с подменой техосмотра")
class AcceptanceServiceTest {

    private static final ServiceCenter ALWAYS_PASS =
            vehicle -> InspectionResult.pass();
    private static final ServiceCenter ALWAYS_FAIL =
            vehicle -> InspectionResult.fail("изношены тормоза");
    private static final int SCOOTER_NUMBER = 1001;
    private static final int SIMPLICITY = 8;
    private static final double ENERGY_KWH = 0.6;

    private final AssetRegistry registry = new AssetRegistry();
    private final Vehicle scooter = new ElectricScooter(
            "Ninebot", SCOOTER_NUMBER, SIMPLICITY, ENERGY_KWH);
    private final Helmet helmet = new Helmet("Cairn", SCOOTER_NUMBER + 1);

    @Test
    @DisplayName("Прошедший техосмотр транспорт попадает на баланс")
    void vehicleThatPassedInspectionIsAddedToFleet() {
        final AcceptanceService service =
                new AcceptanceService(ALWAYS_PASS, registry);

        final InspectionResult result = service.acceptVehicle(scooter);

        assertTrue(result.passed());
        assertEquals(List.of(scooter), registry.findAll());
    }

    @Test
    @DisplayName("Отклонённый техосмотром транспорт не попадает на баланс")
    void vehicleRejectedByInspectionIsNotAddedToFleet() {
        final AcceptanceService service =
                new AcceptanceService(ALWAYS_FAIL, registry);

        final InspectionResult result = service.acceptVehicle(scooter);

        assertFalse(result.passed());
        assertEquals("изношены тормоза", result.reason());
        assertTrue(registry.findAll().isEmpty());
    }

    @Test
    @DisplayName("На техосмотр уходит именно принимаемый транспорт")
    void serviceCenterInspectsExactlyTheIncomingVehicle() {
        final ServiceCenter serviceCenter = mock(ServiceCenter.class);
        when(serviceCenter.inspect(scooter))
                .thenReturn(InspectionResult.pass());

        new AcceptanceService(serviceCenter, registry).acceptVehicle(scooter);

        verify(serviceCenter).inspect(scooter);
    }

    @Test
    @DisplayName("Занятый номер отклоняется ещё до техосмотра")
    void duplicateNumberIsRejectedBeforeInspection() {
        final ServiceCenter serviceCenter = mock(ServiceCenter.class);
        final AcceptanceService service =
                new AcceptanceService(serviceCenter, registry);
        registry.add(new Helmet("Cairn", SCOOTER_NUMBER));

        assertThrows(DuplicateInventoryNumberException.class,
                () -> service.acceptVehicle(scooter));
        verifyNoInteractions(serviceCenter);
    }

    @Test
    @DisplayName("Вещи ставятся на баланс без техосмотра")
    void thingsAreAcceptedWithoutInspection() {
        final ServiceCenter serviceCenter = mock(ServiceCenter.class);

        new AcceptanceService(serviceCenter, registry).acceptThing(helmet);

        assertEquals(List.of(helmet), registry.findAll());
        verifyNoInteractions(serviceCenter);
    }

    @Test
    @DisplayName("Вещь с занятым номером отклоняется")
    void thingWithTakenNumberIsRejected() {
        final AcceptanceService service =
                new AcceptanceService(ALWAYS_PASS, registry);
        service.acceptVehicle(scooter);
        final Helmet sameNumber = new Helmet("Cairn", SCOOTER_NUMBER);

        assertThrows(DuplicateInventoryNumberException.class,
                () -> service.acceptThing(sameNumber));
    }
}
