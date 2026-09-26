package studying.withsolid.service.impl;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.format.DateTimeFormatter;
import studying.withsolid.exception.ApplicationErrorCode;
import studying.withsolid.exception.ApplicationException;
import studying.withsolid.model.Report;
import studying.withsolid.service.ReportSaver;

public class ReportSaverImpl implements ReportSaver {
    private static final DateTimeFormatter DATE_TIME_FORMATTER = DateTimeFormatter.ofPattern("HH-mm-ss");

    @Override
    public void save(Report report) {
        if (report == null) {
            throw new ApplicationException(
                    ApplicationErrorCode.VALIDATION_ERROR,
                    "Отчет не может быть null"
            );
        }

        var reportForSave = Path.of("modules/practise-2/reports", "report-%s-%s.txt".formatted(
                report.date(),
                report.time().format(DATE_TIME_FORMATTER)
        ));

        try {
            Files.createDirectories(reportForSave.getParent());
            Files.writeString(reportForSave, report.toString());
        } catch (IOException exception) {
            throw new ApplicationException(ApplicationErrorCode.FILE_WRITE_ERROR,
                    "Не удалось сохранить отчёт в " + reportForSave, exception);
        }
    }
}
