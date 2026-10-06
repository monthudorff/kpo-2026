package ru.hse.vyshkat.fleet;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.List;
import java.util.Map;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import ru.hse.vyshkat.domain.Asset;
import ru.hse.vyshkat.domain.thing.ChargingCabinet;
import ru.hse.vyshkat.domain.thing.DockStation;
import ru.hse.vyshkat.domain.thing.Helmet;
import ru.hse.vyshkat.domain.vehicle.Bicycle;
import ru.hse.vyshkat.domain.vehicle.ElectricBike;
import ru.hse.vyshkat.domain.vehicle.ElectricScooter;
import ru.hse.vyshkat.domain.vehicle.Vehicle;
import ru.hse.vyshkat.fleet.FleetReportService.EnergyReport;
import ru.hse.vyshkat.fleet.FleetReportService.FleetSummary;

/** Unit-тесты отчётов по балансу. */
@DisplayName("Отчёты по балансу")
class FleetReportServiceTest {

    private static final double EPS = 1e-9;
    private static final double SCOOTER_KWH = 0.6;
    private static final double EBIKE_KWH = 0.9;
    private static final double DOCK_KWH = 1.2;
    private static final double CABINET_KWH = 4.5;
    private static final int TOTAL_UNITS = 6;
    private static final int UNITS_PER_GROUP = 3;

    private final ElectricScooter scooter =
            new ElectricScooter("Ninebot", 1001, 8, SCOOTER_KWH);
    private final ElectricBike eBike =
            new ElectricBike("Eltreco", 1002, 5, EBIKE_KWH);
    private final Bicycle bicycle = new Bicycle("Stels", 1003, 6);
    private final Helmet helmet = new Helmet("Cairn", 2001);
    private final DockStation dock =
            new DockStation("Покровка", 2002, DOCK_KWH);
    private final ChargingCabinet cabinet =
            new ChargingCabinet("Склад", 2003, CABINET_KWH);

    private final AssetRegistry registry = new AssetRegistry();
    private final FleetReportService reports = new FleetReportService(
            registry, new SimplicityThresholdPolicy(
                    SimplicityThresholdPolicy.DEFAULT_MIN_SIMPLICITY));

    private void fillFleet() {
        List.<Asset>of(cabinet, bicycle, scooter, helmet, eBike, dock)
                .forEach(registry::add);
    }

    @Test
    @DisplayName("Сводка считает транспорт, вещи и виды")
    void summaryCountsVehiclesThingsAndKinds() {
        fillFleet();

        final FleetSummary summary = reports.summary();

        assertEquals(TOTAL_UNITS, summary.total());
        assertEquals(UNITS_PER_GROUP, summary.vehicles());
        assertEquals(UNITS_PER_GROUP, summary.things());
        assertEquals(1L, summary.countByKind().get("Электросамокат"));
        assertEquals(TOTAL_UNITS, summary.countByKind().size());
    }

    @Test
    @DisplayName("Энергию суммируют только потребители энергии")
    void energyReportSumsOnlyEnergyConsumers() {
        fillFleet();

        final EnergyReport energy = reports.energyReport();

        assertEquals(SCOOTER_KWH + EBIKE_KWH, energy.vehiclesKwh(), EPS);
        assertEquals(DOCK_KWH + CABINET_KWH, energy.thingsKwh(), EPS);
        assertEquals(SCOOTER_KWH + EBIKE_KWH + DOCK_KWH + CABINET_KWH,
                energy.totalKwh(), EPS);
    }

    @Test
    @DisplayName("Новичкам — только транспорт с простотой ≥ 6")
    void beginnerListContainsOnlyVehiclesWithSimplicityAtLeastSix() {
        fillFleet();

        assertEquals(List.<Vehicle>of(scooter, bicycle),
                reports.beginnerFriendlyVehicles());
        assertEquals("простота для новичка ≥ 6", reports.beginnerRule());
    }

    @Test
    @DisplayName("Отчёт следует внедрённому правилу для новичков")
    void beginnerListFollowsInjectedPolicy() {
        fillFleet();
        final BeginnerPolicy bicyclesOnly = new BeginnerPolicy() {
            @Override
            public boolean isSuitableForBeginner(final Vehicle vehicle) {
                return vehicle instanceof Bicycle;
            }

            @Override
            public String description() {
                return "только велосипеды";
            }
        };

        final FleetReportService customReports =
                new FleetReportService(registry, bicyclesOnly);

        assertEquals(List.<Vehicle>of(bicycle),
                customReports.beginnerFriendlyVehicles());
    }

    @Test
    @DisplayName("Ведомость содержит технику и вещи по номерам")
    void inventoryListShowsVehiclesAndThingsByNumber() {
        fillFleet();

        assertEquals(List.of(scooter, eBike, bicycle, helmet, dock, cabinet),
                reports.inventoryList());
    }

    @Test
    @DisplayName("Пустой парк даёт нулевые отчёты")
    void emptyFleetGivesZeroReports() {
        assertEquals(new FleetSummary(0, 0, 0, Map.of()), reports.summary());
        assertEquals(0.0, reports.energyReport().totalKwh(), EPS);
        assertTrue(reports.beginnerFriendlyVehicles().isEmpty());
        assertTrue(reports.inventoryList().isEmpty());
    }
}
