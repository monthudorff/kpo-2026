package ru.hse.vyshkat.fleet;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.List;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import ru.hse.vyshkat.domain.Asset;
import ru.hse.vyshkat.domain.EnergyConsumer;
import ru.hse.vyshkat.domain.thing.DockStation;
import ru.hse.vyshkat.domain.thing.Helmet;
import ru.hse.vyshkat.domain.vehicle.Bicycle;
import ru.hse.vyshkat.domain.vehicle.ElectricScooter;
import ru.hse.vyshkat.domain.vehicle.Vehicle;

/** Unit-тесты баланса. */
@DisplayName("Баланс (AssetRegistry)")
class AssetRegistryTest {

    private static final int SIMPLICITY = 8;
    private static final double ENERGY_KWH = 0.6;

    private final AssetRegistry registry = new AssetRegistry();

    private final ElectricScooter scooter =
            new ElectricScooter("Ninebot", 10, SIMPLICITY, ENERGY_KWH);
    private final Helmet helmet = new Helmet("Cairn", 20);
    private final Bicycle bicycle = new Bicycle("Stels", 30, SIMPLICITY);
    private final DockStation dock =
            new DockStation("Покровка", 40, ENERGY_KWH);

    @Test
    @DisplayName("Ведомость упорядочена по инвентарному номеру")
    void listsAssetsSortedByInventoryNumber() {
        registry.add(bicycle);
        registry.add(scooter);
        registry.add(helmet);

        assertEquals(List.of(scooter, helmet, bicycle), registry.findAll());
    }

    @Test
    @DisplayName("Повторный инвентарный номер отклоняется")
    void rejectsDuplicateInventoryNumber() {
        registry.add(scooter);
        final Helmet sameNumber =
                new Helmet("Другой шлем", scooter.getInventoryNumber());

        assertThrows(DuplicateInventoryNumberException.class,
                () -> registry.add(sameNumber));
        assertEquals(List.of(scooter), registry.findAll());
    }

    @Test
    @DisplayName("Поиск работает и по классу, и по интерфейсу-возможности")
    void findsAssetsByClassOrCapabilityInterface() {
        List.<Asset>of(bicycle, scooter, helmet, dock).forEach(registry::add);

        assertEquals(List.<Vehicle>of(scooter, bicycle),
                registry.findAllOf(Vehicle.class));
        assertEquals(List.<EnergyConsumer>of(scooter, dock),
                registry.findAllOf(EnergyConsumer.class));
    }

    @Test
    @DisplayName("Баланс сообщает, занят ли номер")
    void reportsWhetherNumberIsTaken() {
        registry.add(helmet);

        assertTrue(registry.contains(helmet.getInventoryNumber()));
        assertFalse(registry.contains(helmet.getInventoryNumber() + 1));
    }

    @Test
    @DisplayName("Список из ведомости нельзя изменить снаружи")
    void returnedListCannotModifyRegistry() {
        registry.add(helmet);
        final List<Asset> all = registry.findAll();

        assertThrows(UnsupportedOperationException.class,
                () -> all.add(bicycle));
    }
}
