package ru.hse.vyshkat.fleet;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.TreeMap;
import java.util.stream.Collectors;
import ru.hse.vyshkat.domain.Asset;
import ru.hse.vyshkat.domain.EnergyConsumer;
import ru.hse.vyshkat.domain.thing.Thing;
import ru.hse.vyshkat.domain.vehicle.Vehicle;

/**
 * Отчёты по балансу. Только считает данные — форматирует их консоль.
 */
public final class FleetReportService {

    /** Баланс, по которому строятся отчёты. */
    private final AssetRegistry registry;
    /** Действующее правило для новичков. */
    private final BeginnerPolicy beginnerPolicy;

    /**
     * Создаёт сервис отчётов.
     *
     * @param assetRegistry баланс
     * @param policy        правило для новичков
     */
    public FleetReportService(final AssetRegistry assetRegistry,
                              final BeginnerPolicy policy) {
        this.registry = Objects.requireNonNull(assetRegistry, "assetRegistry");
        this.beginnerPolicy = Objects.requireNonNull(policy, "policy");
    }

    /**
     * Считает единицы на балансе: всего, транспорта, вещей и по видам.
     *
     * @return сводка по количеству
     */
    public FleetSummary summary() {
        List<Asset> all = registry.findAll();
        Map<String, Long> countByKind = all.stream()
                .collect(Collectors.groupingBy(Asset::getKind,
                        TreeMap::new, Collectors.counting()));
        return new FleetSummary(
                all.size(),
                registry.findAllOf(Vehicle.class).size(),
                registry.findAllOf(Thing.class).size(),
                countByKind);
    }

    /**
     * Считает суточное энергопотребление транспорта и оборудования.
     *
     * @return расход по группам и итог
     */
    public EnergyReport energyReport() {
        return new EnergyReport(
                sumDailyEnergy(Vehicle.class), sumDailyEnergy(Thing.class));
    }

    /**
     * Отбирает транспорт, который можно выдавать новичкам.
     *
     * @return подходящий транспорт по возрастанию номера
     */
    public List<Vehicle> beginnerFriendlyVehicles() {
        return registry.findAllOf(Vehicle.class).stream()
                .filter(beginnerPolicy::isSuitableForBeginner)
                .toList();
    }

    /**
     * Возвращает формулировку действующего правила для новичков.
     *
     * @return правило, например «простота для новичка ≥ 6»
     */
    public String beginnerRule() {
        return beginnerPolicy.description();
    }

    /**
     * Строит инвентарную ведомость: всю технику и вещи.
     *
     * @return всё имущество по возрастанию номера
     */
    public List<Asset> inventoryList() {
        return registry.findAll();
    }

    private double sumDailyEnergy(final Class<? extends Asset> branch) {
        return registry.findAllOf(branch).stream()
                .filter(EnergyConsumer.class::isInstance)
                .map(EnergyConsumer.class::cast)
                .mapToDouble(EnergyConsumer::getDailyEnergyKwh)
                .sum();
    }

    /**
     * Сводка по количеству единиц.
     *
     * @param total       всего единиц
     * @param vehicles    единиц транспорта
     * @param things      вещей
     * @param countByKind количество по видам, по алфавиту
     */
    public record FleetSummary(int total, int vehicles, int things,
                               Map<String, Long> countByKind) {

        /**
         * Делает копию счётчиков по видам: отсортированную и неизменяемую.
         *
         * @param total       всего единиц
         * @param vehicles    единиц транспорта
         * @param things      вещей
         * @param countByKind количество по видам
         */
        public FleetSummary {
            countByKind = Collections.unmodifiableSortedMap(
                    new TreeMap<>(countByKind));
        }
    }

    /**
     * Суточный расход энергии, кВт·ч.
     *
     * @param vehiclesKwh расход транспорта
     * @param thingsKwh   расход оборудования
     */
    public record EnergyReport(double vehiclesKwh, double thingsKwh) {

        /**
         * Считает общий расход.
         *
         * @return расход транспорта и оборудования вместе
         */
        public double totalKwh() {
            return vehiclesKwh + thingsKwh;
        }
    }
}
