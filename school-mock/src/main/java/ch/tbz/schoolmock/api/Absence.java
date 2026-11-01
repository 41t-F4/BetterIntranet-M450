package ch.tbz.schoolmock.api;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;

import com.fasterxml.jackson.annotation.JsonFormat;

/**
 * One absence entry of the user, as served by {@code GET /mock/users/{username}/absences}.
 *
 * <p>Times are serialized as {@code HH:mm}, dates as ISO {@code yyyy-MM-dd} and the report
 * timestamp as ISO local date-time with seconds ({@code 2026-10-12T10:00:00}).
 *
 * @param id          identifier of the absence entry
 * @param date        day of the missed lesson
 * @param lessonStart start time of the missed lesson block
 * @param lessonEnd   end time of the missed lesson block
 * @param subject     subject or module that was missed
 * @param teacher     short code of the teacher who recorded the absence
 * @param status      processing status of the entry
 * @param reason      justification handed in by the student, {@code null} while none was given
 * @param reportedAt  when the teacher recorded the absence
 */
public record Absence(
		long id,
		@JsonFormat(pattern = "yyyy-MM-dd") LocalDate date,
		@JsonFormat(pattern = "HH:mm") LocalTime lessonStart,
		@JsonFormat(pattern = "HH:mm") LocalTime lessonEnd,
		String subject,
		String teacher,
		AbsenceStatus status,
		String reason,
		@JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss") LocalDateTime reportedAt) {
}
