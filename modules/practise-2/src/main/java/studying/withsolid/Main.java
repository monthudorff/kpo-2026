package studying.withsolid;

import studying.withsolid.model.Report;

import java.time.LocalDateTime;
import studying.withsolid.service.ReportServiceFacade;
import studying.withsolid.service.impl.ReportSaverImpl;
import studying.withsolid.service.impl.ReportSenderImpl;

public class Main {
    /**
     * Runs the report creation, persistence, and delivery demonstration.
     */
    static void main() {
        var now = LocalDateTime.now();
        var report = Report.builder()
                .title("Отчёт")
                .date(now.toLocalDate())
                .time(now.toLocalTime())
                .carsSold(100)
                .motorcyclesSold(50)
                .build();

        var reportService = new ReportServiceFacade(
                new ReportSenderImpl(),
                new ReportSaverImpl()
        );

        reportService.process(report, "example@example.com");
    }
}
