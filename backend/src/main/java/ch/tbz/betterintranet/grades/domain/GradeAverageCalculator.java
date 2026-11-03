package ch.tbz.betterintranet.grades.domain;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Collection;
import java.util.OptionalDouble;

/**
 * Calculates averages of Swiss school grades (1.0 to 6.0). All arithmetic is done in {@link BigDecimal} so
 * that half-up rounding works on the decimal value a user sees, not on a binary approximation of it.
 */
public final class GradeAverageCalculator {

    private static final int SCALE = 2;
    private static final RoundingMode ROUNDING = RoundingMode.HALF_UP;

    private GradeAverageCalculator() {
    }

    /**
     * Weighted average {@code sum(grade * weight) / sum(weight)}, rounded half-up to two decimals.
     * Entries with weight 0 do not count.
     *
     * @return the average, or empty when the total weight is 0 (no entries or only weight-0 entries)
     */
    public static OptionalDouble weightedAverage(Collection<WeightedGrade> grades) {
        BigDecimal weightedSum = BigDecimal.ZERO;
        BigDecimal totalWeight = BigDecimal.ZERO;
        for (WeightedGrade entry : grades) {
            BigDecimal weight = BigDecimal.valueOf(entry.weight());
            weightedSum = weightedSum.add(BigDecimal.valueOf(entry.grade()).multiply(weight));
            totalWeight = totalWeight.add(weight);
        }
        if (totalWeight.signum() == 0) {
            return OptionalDouble.empty();
        }
        return OptionalDouble.of(weightedSum.divide(totalWeight, SCALE, ROUNDING).doubleValue());
    }

    /**
     * Plain (unweighted) mean, rounded half-up to two decimals.
     *
     * @return the mean, or empty when there are no values
     */
    public static OptionalDouble mean(Collection<Double> values) {
        if (values.isEmpty()) {
            return OptionalDouble.empty();
        }
        BigDecimal sum = BigDecimal.ZERO;
        for (double value : values) {
            sum = sum.add(BigDecimal.valueOf(value));
        }
        return OptionalDouble.of(sum.divide(BigDecimal.valueOf(values.size()), SCALE, ROUNDING).doubleValue());
    }

    /** Rounds half-up to two decimals, e.g. {@code 4.8333 -> 4.83}, {@code 5.165 -> 5.17}. */
    public static double roundToTwoDecimals(double value) {
        return BigDecimal.valueOf(value).setScale(SCALE, ROUNDING).doubleValue();
    }

    /**
     * Rounds to the nearest half grade as printed on a Swiss report card (Zeugnisnote):
     * {@code 5.24 -> 5.0}, {@code 5.25 -> 5.5}, {@code 5.74 -> 5.5}, {@code 5.75 -> 6.0}.
     *
     * <p>Only values on the Swiss scale are accepted. For those the result is always between 1.0 and 6.0:
     * the arithmetic is done in {@link BigDecimal}, so there are no binary rounding effects, and rounding a
     * value in [1.0, 6.0] to the nearest half step cannot leave that range.
     *
     * @throws IllegalArgumentException if {@code value} is not a finite number between 1.0 and 6.0
     */
    public static double roundToSwissHalf(double value) {
        if (!Double.isFinite(value) || value < WeightedGrade.MIN_GRADE || value > WeightedGrade.MAX_GRADE) {
            throw new IllegalArgumentException("a Zeugnisnote can only be derived from values between 1.0 and 6.0 but was "
                    + value);
        }
        return BigDecimal.valueOf(value)
                .multiply(BigDecimal.TWO)
                .setScale(0, ROUNDING)
                .divide(BigDecimal.TWO)
                .doubleValue();
    }
}
