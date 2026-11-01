package ch.tbz.betterintranet.absences.api;

/**
 * Aggregated numbers over all absences of a student.
 *
 * @param total        number of absence entries
 * @param open         entries with status {@link AbsenceStatus#OPEN}
 * @param excused      entries with status {@link AbsenceStatus#EXCUSED}
 * @param unexcused    entries with status {@link AbsenceStatus#UNEXCUSED}
 * @param totalLessons number of 45-minute lessons covered by all entries
 */
public record AbsenceSummary(int total, int open, int excused, int unexcused, int totalLessons) {
}
