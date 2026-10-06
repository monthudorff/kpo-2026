package ru.hse.vyshkat.domain;

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.List;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.NullAndEmptySource;
import org.junit.jupiter.params.provider.ValueSource;
import ru.hse.vyshkat.domain.thing.ChargingCabinet;
import ru.hse.vyshkat.domain.thing.DockStation;
import ru.hse.vyshkat.domain.thing.Helmet;
import ru.hse.vyshkat.domain.vehicle.Bicycle;
import ru.hse.vyshkat.domain.vehicle.ElectricBike;
import ru.hse.vyshkat.domain.vehicle.ElectricScooter;
import ru.hse.vyshkat.domain.vehicle.Vehicle;

/** Unit-тесты доменной модели: инварианты, виды и возможности. */
@DisplayName("Доменная модель")
class AssetTest {

    private static final int NUMBER = 1001;
    private static final int SIMPLICITY = 8;
    private static final double ENERGY_KWH = 0.6;

    @Test
    @DisplayName("Электросамокат хранит все атрибуты, имя без пробелов")
    void electricScooterKeepsAllAttributes() {
        final ElectricScooter scooter = new ElectricScooter(
                "  Ninebot Max G30 ", NUMBER, SIMPLICITY, ENERGY_KWH);

        assertAll(
                () -> assertEquals("Ninebot Max G30", scooter.getName()),
                () -> assertEquals(NUMBER, scooter.getInventoryNumber()),
                () -> assertEquals(SIMPLICITY,
                        scooter.getBeginnerSimplicity()),
                () -> assertEquals(ENERGY_KWH, scooter.getDailyEnergyKwh()),
                () -> assertEquals(
                        "Электросамокат «Ninebot Max G30», инв. № 1001",
                        scooter.toString()));
    }

    @Test
    @DisplayName("Энергию потребляет только техника с питанием")
    void onlyPoweredAssetsAreEnergyConsumers() {
        final List<Asset> powered = List.of(
                new ElectricScooter("Ninebot", 1, SIMPLICITY, ENERGY_KWH),
                new ElectricBike("Eltreco", 2, SIMPLICITY, ENERGY_KWH),
                new DockStation("Покровка", 3, ENERGY_KWH),
                new ChargingCabinet("Склад", 4, ENERGY_KWH));
        final List<Asset> unpowered = List.of(
                new Bicycle("Stels", 5, SIMPLICITY),
                new Helmet("Cairn", 6));

        assertAll(
                () -> assertTrue(powered.stream()
                        .allMatch(EnergyConsumer.class::isInstance)),
                () -> assertFalse(unpowered.stream()
                        .anyMatch(EnergyConsumer.class::isInstance)));
    }

    @Test
    @DisplayName("Каждый вид техники и вещей называет себя в отчётах")
    void everyAssetHasItsOwnKind() {
        final List<Asset> assets = List.of(
                new Bicycle("Stels", 1, SIMPLICITY),
                new ElectricScooter("Ninebot", 2, SIMPLICITY, ENERGY_KWH),
                new ElectricBike("Eltreco", 3, SIMPLICITY, ENERGY_KWH),
                new Helmet("Cairn", 4),
                new DockStation("Покровка", 5, ENERGY_KWH),
                new ChargingCabinet("Склад", 6, ENERGY_KWH));

        assertEquals(List.of("Велосипед", "Электросамокат", "Электровелосипед",
                        "Шлем", "Док-станция", "Зарядный шкаф"),
                assets.stream().map(Asset::getKind).toList());
    }

    @ParameterizedTest(name = "простота {0} допустима")
    @ValueSource(ints = {Vehicle.MIN_SIMPLICITY, Vehicle.MAX_SIMPLICITY})
    @DisplayName("Границы шкалы простоты 1 и 10 допустимы")
    void simplicityBoundariesAreAccepted(final int simplicity) {
        assertEquals(simplicity,
                new Bicycle("Stels", 1, simplicity).getBeginnerSimplicity());
    }

    @ParameterizedTest(name = "простота {0} отклоняется")
    @ValueSource(ints = {
            -1, Vehicle.MIN_SIMPLICITY - 1, Vehicle.MAX_SIMPLICITY + 1})
    @DisplayName("Простота вне шкалы 1–10 отклоняется")
    void simplicityOutsideScaleIsRejected(final int simplicity) {
        assertThrows(IllegalArgumentException.class,
                () -> new Bicycle("Stels", 1, simplicity));
    }

    @ParameterizedTest(name = "номер {0} отклоняется")
    @ValueSource(ints = {-1, 0})
    @DisplayName("Инвентарный номер должен быть положительным")
    void inventoryNumberMustBePositive(final int number) {
        assertThrows(IllegalArgumentException.class,
                () -> new Helmet("Cairn", number));
    }

    @ParameterizedTest(name = "наименование «{0}» отклоняется")
    @NullAndEmptySource
    @ValueSource(strings = {"   "})
    @DisplayName("Наименование не может быть пустым")
    void nameMustNotBeBlank(final String name) {
        assertThrows(IllegalArgumentException.class,
                () -> new Helmet(name, 1));
    }

    @ParameterizedTest(name = "расход {0} отклоняется")
    @ValueSource(doubles = {0, -1, Double.NaN, Double.POSITIVE_INFINITY})
    @DisplayName("Суточный расход должен быть положительным числом")
    void dailyEnergyMustBePositive(final double kwh) {
        assertAll(
                () -> assertThrows(IllegalArgumentException.class,
                        () -> new ElectricBike("Eltreco", 1, SIMPLICITY, kwh)),
                () -> assertThrows(IllegalArgumentException.class,
                        () -> new DockStation("Покровка", 2, kwh)),
                () -> assertThrows(IllegalArgumentException.class,
                        () -> new ChargingCabinet("Склад", 2, kwh)));
    }
}
