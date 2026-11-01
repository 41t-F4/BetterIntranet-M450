package ch.tbz.betterintranet.grades.domain;

/** A grade together with its weight, the input of {@link GradeAverageCalculator}. */
public record WeightedGrade(double grade, double weight) {
}
