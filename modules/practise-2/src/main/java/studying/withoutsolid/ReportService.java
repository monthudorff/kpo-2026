package studying.withoutsolid;

import java.io.IOException;
import java.io.UncheckedIOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.LocalDateTime;

/**
 * Intentionally monolithic report service used to demonstrate an implementation without SOLID.
 * It generates, persists, and sends a report by itself.
 */
public class ReportService {
    /**
     * Creates a text report about sales at the current moment.
     *
     * @return generated report text
     */
    public String generateReport() {
        var now = LocalDateTime.now();

        return "Отчёт%nДата: %tF%nВремя: %tT%n--------------------------------%n"
                .formatted(now, now)
                + "Продано автомобилей: 100 шт.%nПродано мотоциклов: 50 шт.%n--------------------------------%n";
    }

    /**
     * Writes report text to the selected file.
     *
     * @param report report text to save
     * @param fileName destination file name
     * @throws UncheckedIOException when the file cannot be written
     */
    public void saveReport(String report, String fileName) {
        try {
            Files.writeString(Path.of(fileName), report);
        } catch (IOException exception) {
            throw new UncheckedIOException("Не удалось сохранить отчёт в " + fileName, exception);
        }
    }

    /**
     * Imitates sending a report to an email recipient.
     *
     * @param report report text to send
     * @param email recipient email address
     */
    public void sendReport(String report, String email) {
        System.out.printf("Отправка отчёта %s на email: %s%n", report, email);
    }
}
