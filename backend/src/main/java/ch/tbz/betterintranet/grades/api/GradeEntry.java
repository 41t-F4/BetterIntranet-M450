package ch.tbz.betterintranet.grades.api;

import java.time.LocalDate;

/** One graded piece of work. */
public record GradeEntry(long id, String title, LocalDate date, double grade, double weight) {
}
