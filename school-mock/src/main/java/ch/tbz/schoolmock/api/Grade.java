package ch.tbz.schoolmock.api;

import java.time.LocalDate;

import com.fasterxml.jackson.annotation.JsonFormat;

/**
 * One graded assessment of the user, as served by {@code GET /mock/users/{username}/grades}.
 *
 * <p>Grades follow the Swiss scale from {@code 1.0} to {@code 6.0} with one decimal, {@code 4.0}
 * being the pass mark. The mock serves the flat list only; averages are computed by the main
 * backend.
 *
 * @param id         identifier of the assessment
 * @param subject    subject or module name, e.g. {@code Modul 450 Applikationen testen}
 * @param semester   semester of the assessment, e.g. {@code HS 2026} or {@code FS 2026}
 * @param title      title of the assessment, e.g. {@code LB1 Testkonzept}
 * @param date       day of the assessment
 * @param grade      achieved grade between {@code 1.0} and {@code 6.0}
 * @param weight     weight of the assessment within the subject, usually {@code 1.0}
 */
public record Grade(
		long id,
		String subject,
		String semester,
		String title,
		@JsonFormat(pattern = "yyyy-MM-dd") LocalDate date,
		double grade,
		double weight) {
}
