package studying.withoutsolid;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.nio.file.Files;
import java.nio.file.Path;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class ReportServiceTest {
    @TempDir
    Path temporaryDirectory;

    @Test
    @DisplayName("Формирует и сохраняет отчёт в файл")
    void generatesAndSavesReport() throws Exception {
        var reportService = new ReportService();
        var report = reportService.generateReport();
        var file = temporaryDirectory.resolve("report.txt");

        reportService.saveReport(report, file.toString());

        assertTrue(report.contains("Продано автомобилей: 100 шт."));
        assertEquals(report, Files.readString(file));
    }
}
