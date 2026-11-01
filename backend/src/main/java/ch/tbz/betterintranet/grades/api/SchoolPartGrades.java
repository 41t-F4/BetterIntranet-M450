package ch.tbz.betterintranet.grades.api;

import java.util.List;

/**
 * All subjects of one school part.
 *
 * @param schoolPart the school part
 * @param label      German UI label of the school part, e.g. {@code ÜK}
 * @param average    plain mean of the subject averages, {@code null} when no subject has an average
 * @param subjects   subjects sorted by name, newest semester first
 */
public record SchoolPartGrades(SchoolPart schoolPart, String label, Double average, List<SubjectGrades> subjects) {

    /** Derives the label from the school part. */
    public SchoolPartGrades(SchoolPart schoolPart, Double average, List<SubjectGrades> subjects) {
        this(schoolPart, schoolPart.label(), average, subjects);
    }
}
