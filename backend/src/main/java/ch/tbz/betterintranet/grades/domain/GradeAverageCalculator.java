package ch.tbz.betterintranet.grades.domain;

import java.util.Collection;
import java.util.OptionalDouble;

/** Calculates weighted and plain averages of Swiss school grades (1.0 to 6.0). */
public final class GradeAverageCalculator {

    private GradeAverageCalculator() {
    }

    public static OptionalDouble weightedAverage(Collection<WeightedGrade> grades) {
        throw new UnsupportedOperationException("not implemented");
    }

    public static OptionalDouble mean(Collection<Double> values) {
        throw new UnsupportedOperationException("not implemented");
    }

    public static double roundToTwoDecimals(double value) {
        throw new UnsupportedOperationException("not implemented");
    }

    public static double roundToSwissHalf(double value) {
        throw new UnsupportedOperationException("not implemented");
    }
}
