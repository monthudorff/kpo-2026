package ru.hse.vyshkat.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import ru.hse.vyshkat.fleet.AcceptanceService;
import ru.hse.vyshkat.fleet.AssetRegistry;
import ru.hse.vyshkat.fleet.BeginnerPolicy;
import ru.hse.vyshkat.fleet.FleetReportService;
import ru.hse.vyshkat.fleet.SimplicityThresholdPolicy;
import ru.hse.vyshkat.inspection.ServiceCenter;
import ru.hse.vyshkat.inspection.StandardServiceCenter;

/**
 * Composition root бизнес-логики: единственное место, где выбираются
 * реализации абстракций. Доменные классы и сервисы о Spring не знают —
 * зависимости приходят к ним через конструкторы.
 */
@Configuration(proxyBeanMethods = false)
public final class FleetConfig {

    /** Норма суточного расхода электротранспорта, кВт·ч: выше — неисправен. */
    public static final double MAX_VEHICLE_DAILY_ENERGY_KWH = 1.5;

    /**
     * Штатный техосмотр с нормой расхода энергии.
     *
     * @return техосмотр
     */
    @Bean
    ServiceCenter serviceCenter() {
        return new StandardServiceCenter(MAX_VEHICLE_DAILY_ENERGY_KWH);
    }

    /**
     * Правило для новичков из требований заказчика: простота ≥ 6.
     *
     * @return правило для новичков
     */
    @Bean
    BeginnerPolicy beginnerPolicy() {
        return new SimplicityThresholdPolicy(
                SimplicityThresholdPolicy.DEFAULT_MIN_SIMPLICITY);
    }

    /**
     * Баланс — один на всё приложение (синглтон контейнера).
     *
     * @return баланс
     */
    @Bean
    AssetRegistry assetRegistry() {
        return new AssetRegistry();
    }

    /**
     * Приёмка: контейнер передаёт ей техосмотр и баланс.
     *
     * @param serviceCenter техосмотр
     * @param assetRegistry баланс
     * @return сервис приёмки
     */
    @Bean
    AcceptanceService acceptanceService(final ServiceCenter serviceCenter,
                                        final AssetRegistry assetRegistry) {
        return new AcceptanceService(serviceCenter, assetRegistry);
    }

    /**
     * Отчёты: контейнер передаёт им тот же баланс и правило для новичков.
     *
     * @param assetRegistry  баланс
     * @param beginnerPolicy правило для новичков
     * @return сервис отчётов
     */
    @Bean
    FleetReportService fleetReportService(
            final AssetRegistry assetRegistry,
            final BeginnerPolicy beginnerPolicy) {
        return new FleetReportService(assetRegistry, beginnerPolicy);
    }
}
