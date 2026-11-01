package ch.tbz.betterintranet.grades.api;

import java.util.List;

/** All grades of one subject in one semester. {@code average} and {@code reportGrade} are {@code null} when there are no weighted grades. */
public record SubjectGrades(String subject, String semester, Double average, Double reportGrade, List<GradeEntry> grades) {
}
