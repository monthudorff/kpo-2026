package studying.service.impl;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import studying.withsolid.exception.ApplicationErrorCode;
import studying.withsolid.exception.ApplicationException;
import studying.withsolid.model.Report;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.nio.charset.StandardCharsets;
import java.time.LocalDate;
import java.time.LocalTime;
import studying.withsolid.service.impl.ReportSenderImpl;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

class EmailReportSenderTest {
    @Test
    @DisplayName("Выводит адрес получателя и заголовок отчёта при отправке")
    void sendPrintsDeliveryMessage() {
        var originalOutput = System.out;
        var output = new ByteArrayOutputStream();
        var report = createReport();

        try (var interceptedOutput = new PrintStream(output, true, StandardCharsets.UTF_8)) {
            System.setOut(interceptedOutput);
            new ReportSenderImpl().send(report, "student@hse.ru");
        } finally {
            System.setOut(originalOutput);
        }

        var message = output.toString(StandardCharsets.UTF_8);
        assertTrue(message.contains("Продажи"));
        assertTrue(message.contains("student@hse.ru"));
    }

    @Test
    @DisplayName("Выбрасывает прикладную ошибку, если email получателя не задан")
    void sendRejectsMissingEmail() {
        var exception = assertThrows(ApplicationException.class,
                () -> new ReportSenderImpl().send(createReport(), null));

        assertEquals(ApplicationErrorCode.VALIDATION_ERROR, exception.getCode());
    }

    private Report createReport() {
        return Report.builder()
                .title("Продажи")
                .date(LocalDate.of(2026, 9, 11))
                .time(LocalTime.NOON)
                .carsSold(100)
                .motorcyclesSold(50)
                .build();
    }
}
