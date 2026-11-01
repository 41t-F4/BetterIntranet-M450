package ch.tbz.betterintranet.grades.api;

import java.time.LocalDate;

/**
 * One graded piece of work.
 *
 * @param id     identifier assigned by the school system
 * @param title  e.g. {@code LB1 Testkonzept}
 * @param date   day the grade was given
 * @param grade  Swiss school grade between 1.0 and 6.0
 * @param weight weight within the subject; 0 means the grade is shown but does not count
 */
public record GradeEntry(long id, String title, LocalDate date, double grade, double weight) {
}
