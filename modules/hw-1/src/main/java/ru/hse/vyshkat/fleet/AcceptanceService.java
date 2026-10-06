package ru.hse.vyshkat.fleet;

import java.util.Objects;
import ru.hse.vyshkat.domain.Asset;
import ru.hse.vyshkat.domain.thing.Thing;
import ru.hse.vyshkat.domain.vehicle.Vehicle;
import ru.hse.vyshkat.inspection.InspectionResult;
import ru.hse.vyshkat.inspection.ServiceCenter;

/**
 * Приёмка имущества на баланс: транспорт — только после техосмотра,
 * вещи — сразу.
 */
public final class AcceptanceService {

    /** Техосмотр, который решает судьбу нового транспорта. */
    private final ServiceCenter serviceCenter;
    /** Баланс, куда попадает принятое имущество. */
    private final AssetRegistry registry;

    /**
     * Создаёт сервис приёмки.
     *
     * @param center        техосмотр
     * @param assetRegistry баланс
     */
    public AcceptanceService(final ServiceCenter center,
                             final AssetRegistry assetRegistry) {
        this.serviceCenter = Objects.requireNonNull(center, "center");
        this.registry = Objects.requireNonNull(assetRegistry, "assetRegistry");
    }

    /**
     * Отправляет транспорт на техосмотр и при успехе ставит его на баланс.
     *
     * @param vehicle новый транспорт
     * @return заключение техосмотра
     * @throws DuplicateInventoryNumberException если номер уже занят;
     *         техосмотр в этом случае не проводится
     */
    public InspectionResult acceptVehicle(final Vehicle vehicle) {
        requireFreeNumber(vehicle);
        InspectionResult result = serviceCenter.inspect(vehicle);
        if (result.passed()) {
            registry.add(vehicle);
        }
        return result;
    }

    /**
     * Ставит вещь на баланс без техосмотра.
     *
     * @param thing новая вещь
     * @throws DuplicateInventoryNumberException если номер уже занят
     */
    public void acceptThing(final Thing thing) {
        requireFreeNumber(thing);
        registry.add(thing);
    }

    private void requireFreeNumber(final Asset asset) {
        Objects.requireNonNull(asset, "asset");
        if (registry.contains(asset.getInventoryNumber())) {
            throw new DuplicateInventoryNumberException(
                    asset.getInventoryNumber());
        }
    }
}
