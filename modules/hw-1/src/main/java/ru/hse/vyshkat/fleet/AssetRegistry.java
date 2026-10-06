package ru.hse.vyshkat.fleet;

import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.TreeMap;
import ru.hse.vyshkat.domain.Asset;

/**
 * Баланс «ВышКат»: всё принятое имущество, упорядоченное по номеру.
 */
public final class AssetRegistry {

    /** Имущество по инвентарному номеру; TreeMap держит номера по порядку. */
    private final Map<Integer, Asset> assetsByNumber = new TreeMap<>();

    /**
     * Ставит объект на баланс.
     *
     * @param asset принятый объект
     * @throws DuplicateInventoryNumberException если номер уже занят
     */
    public void add(final Asset asset) {
        Objects.requireNonNull(asset, "asset");
        int number = asset.getInventoryNumber();
        if (assetsByNumber.putIfAbsent(number, asset) != null) {
            throw new DuplicateInventoryNumberException(number);
        }
    }

    /**
     * Проверяет, занят ли инвентарный номер.
     *
     * @param inventoryNumber номер
     * @return {@code true}, если номер уже на балансе
     */
    public boolean contains(final int inventoryNumber) {
        return assetsByNumber.containsKey(inventoryNumber);
    }

    /**
     * Возвращает всё имущество по возрастанию номера.
     *
     * @return неизменяемый список
     */
    public List<Asset> findAll() {
        return List.copyOf(assetsByNumber.values());
    }

    /**
     * Возвращает объекты заданного типа: класса иерархии ({@code Vehicle})
     * или интерфейса-возможности ({@code EnergyConsumer}).
     *
     * @param <T>  искомый тип
     * @param type класс искомого типа
     * @return неизменяемый список по возрастанию номера
     */
    public <T> List<T> findAllOf(final Class<T> type) {
        return assetsByNumber.values().stream()
                .filter(type::isInstance)
                .map(type::cast)
                .toList();
    }
}
