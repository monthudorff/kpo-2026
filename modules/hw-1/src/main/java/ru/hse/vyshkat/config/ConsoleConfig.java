package ru.hse.vyshkat.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import ru.hse.vyshkat.console.ConsoleApp;
import ru.hse.vyshkat.console.ConsoleIO;
import ru.hse.vyshkat.fleet.AcceptanceService;
import ru.hse.vyshkat.fleet.FleetReportService;

/**
 * Конфигурация консольного приложения поверх {@link FleetConfig}.
 * Бин {@link ConsoleIO} регистрирует {@code Main}: источник ввода зависит
 * от режима запуска.
 */
@Configuration(proxyBeanMethods = false)
@Import(FleetConfig.class)
public final class ConsoleConfig {

    /**
     * Меню оператора.
     *
     * @param acceptanceService  приёмка
     * @param fleetReportService отчёты
     * @param consoleIO          ввод-вывод, зарегистрированный в Main
     * @return консольное приложение
     */
    @Bean
    ConsoleApp consoleApp(final AcceptanceService acceptanceService,
                          final FleetReportService fleetReportService,
                          final ConsoleIO consoleIO) {
        return new ConsoleApp(
                acceptanceService, fleetReportService, consoleIO);
    }
}
