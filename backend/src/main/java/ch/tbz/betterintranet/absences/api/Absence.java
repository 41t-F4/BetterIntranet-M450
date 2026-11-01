package ch.tbz.betterintranet.absences.api;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;

/**
 * One absence entry of a student.
 *
 * @param id          identifier assigned by the school system
 * @param date        day of the missed lesson(s)
 * @param lessonStart start of the missed block
 * @param lessonEnd   end of the missed block
 * @param subject     subject name, e.g. "Englisch"
 * @param teacher     teacher short name, e.g. "MID"
 * @param status      open, excused or unexcused
 * @param reason      reason given by the student, may be null
 * @param reportedAt  when the absence was recorded
 */
public record Absence(long id, LocalDate date, LocalTime lessonStart, LocalTime lessonEnd, String subject,
                      String teacher, AbsenceStatus status, String reason, LocalDateTime reportedAt) {
}
