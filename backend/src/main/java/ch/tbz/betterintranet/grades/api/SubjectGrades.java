package ch.tbz.betterintranet.grades.api;

import java.util.List;

/**
 * All grades of one subject in one semester.
 *
 * @param subject     subject name, e.g. {@code Modul 450 Applikationen testen}
 * @param semester    semester label as delivered by the school, e.g. {@code HS 2026}
 * @param average     weighted average of the entries, {@code null} when there is no entry with a weight above 0
 * @param reportGrade {@code average} rounded to the nearest half grade (Zeugnisnote), {@code null} when
 *                    {@code average} is {@code null}
 * @param grades      the entries, newest first
 */
public record SubjectGrades(String subject, String semester, Double average, Double reportGrade, List<GradeEntry> grades) {
}
