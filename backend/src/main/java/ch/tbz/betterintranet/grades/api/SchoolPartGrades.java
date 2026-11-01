package ch.tbz.betterintranet.grades.api;

import java.util.List;

/** All subjects of one school part. {@code average} is {@code null} when no subject has an average. */
public record SchoolPartGrades(SchoolPart schoolPart, String label, Double average, List<SubjectGrades> subjects) {
}
