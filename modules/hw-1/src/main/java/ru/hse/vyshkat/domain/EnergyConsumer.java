package ru.hse.vyshkat.domain;

/**
 * Устройство, потребляющее электроэнергию.
 */
public interface EnergyConsumer {

    /**
     * Возвращает суточный расход энергии.
     *
     * @return расход, кВт·ч в сутки
     */
    double getDailyEnergyKwh();
}
