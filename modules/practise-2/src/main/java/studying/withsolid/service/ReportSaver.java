package studying.withsolid.service;

import studying.withsolid.model.Report;

/** Persists a report in a particular storage format. */
@FunctionalInterface
public interface ReportSaver {
    /**
     * Saves the supplied report.
     *
     * @param report report to persist
     */
    void save(Report report);
}
