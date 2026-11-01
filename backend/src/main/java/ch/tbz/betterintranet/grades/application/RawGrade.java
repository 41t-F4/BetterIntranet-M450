package ch.tbz.betterintranet.grades.application;

import ch.tbz.betterintranet.grades.api.SchoolPart;
import java.time.LocalDate;

/** One grade exactly as the school system delivers it, before grouping and averaging. */
public record RawGrade(long id, SchoolPart schoolPart, String subject, String semester, String title,
                       LocalDate date, double grade, double weight) {
}
