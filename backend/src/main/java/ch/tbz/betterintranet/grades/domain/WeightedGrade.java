package ch.tbz.betterintranet.grades.domain;

/**
 * A grade together with its weight, the input of {@link GradeAverageCalculator}.
 *
 * @param grade  Swiss school grade between 1.0 and 6.0 (inclusive)
 * @param weight non-negative weight; 0 means the grade does not count towards the average
 */
public record WeightedGrade(double grade, double weight) {

    public static final double MIN_GRADE = 1.0;
    public static final double MAX_GRADE = 6.0;

    public WeightedGrade {
        if (grade < MIN_GRADE || grade > MAX_GRADE) {
            throw new IllegalArgumentException("grade must be between 1.0 and 6.0 but was " + grade);
        }
        if (weight < 0) {
            throw new IllegalArgumentException("weight must not be negative but was " + weight);
        }
    }
}
