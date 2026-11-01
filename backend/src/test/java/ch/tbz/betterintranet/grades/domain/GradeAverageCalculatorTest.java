package ch.tbz.betterintranet.grades.domain;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import java.util.List;
import java.util.OptionalDouble;
import java.util.stream.Stream;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.CsvSource;
import org.junit.jupiter.params.provider.MethodSource;
import org.junit.jupiter.params.provider.ValueSource;

class GradeAverageCalculatorTest {

    @Nested
    @DisplayName("weighted average")
    class WeightedAverage {

        static Stream<Arguments> weightedGrades() {
            return Stream.of(
                    Arguments.of("single grade", List.of(new WeightedGrade(5.5, 1.0)), 5.5),
                    Arguments.of("equal weights", List.of(new WeightedGrade(5.0, 1.0), new WeightedGrade(4.0, 1.0)), 4.5),
                    Arguments.of("double weight counts twice",
                            List.of(new WeightedGrade(5.5, 1.0), new WeightedGrade(5.0, 2.0)), 5.17),
                    Arguments.of("fractional weight",
                            List.of(new WeightedGrade(5.0, 1.0), new WeightedGrade(4.5, 0.5)), 4.83),
                    Arguments.of("rounds half up to two decimals",
                            List.of(new WeightedGrade(4.0, 1.0), new WeightedGrade(4.25, 1.0)), 4.13),
                    Arguments.of("all grades identical", List.of(new WeightedGrade(6.0, 3.0), new WeightedGrade(6.0, 0.5)), 6.0));
        }

        @ParameterizedTest(name = "{0}")
        @MethodSource("weightedGrades")
        void shouldWeightGradesAndRoundToTwoDecimals(String description, List<WeightedGrade> grades, double expected) {
            OptionalDouble average = GradeAverageCalculator.weightedAverage(grades);

            assertThat(average).hasValue(expected);
        }

        @Test
        @DisplayName("entries with weight 0 are ignored")
        void shouldIgnoreZeroWeightEntries() {
            List<WeightedGrade> grades = List.of(new WeightedGrade(6.0, 1.0), new WeightedGrade(1.0, 0.0));

            assertThat(GradeAverageCalculator.weightedAverage(grades)).hasValue(6.0);
        }

        @Test
        @DisplayName("only weight-0 entries give no average")
        void shouldBeEmptyWhenAllWeightsAreZero() {
            List<WeightedGrade> grades = List.of(new WeightedGrade(5.0, 0.0), new WeightedGrade(4.0, 0.0));

            assertThat(GradeAverageCalculator.weightedAverage(grades)).isEmpty();
        }

        @Test
        @DisplayName("no entries give no average")
        void shouldBeEmptyWithoutEntries() {
            assertThat(GradeAverageCalculator.weightedAverage(List.of())).isEmpty();
        }

        @ParameterizedTest(name = "grade {0} is rejected")
        @ValueSource(doubles = {0.0, 0.99, 6.01, 7.0, -1.0})
        void shouldRejectGradesOutsideOneToSix(double invalidGrade) {
            assertThatThrownBy(() -> GradeAverageCalculator.weightedAverage(List.of(new WeightedGrade(invalidGrade, 1.0))))
                    .isInstanceOf(IllegalArgumentException.class)
                    .hasMessageContaining("grade");
        }

        @ParameterizedTest(name = "grade {0} is accepted")
        @ValueSource(doubles = {1.0, 6.0, 4.75})
        void shouldAcceptBoundaryGrades(double validGrade) {
            assertThat(GradeAverageCalculator.weightedAverage(List.of(new WeightedGrade(validGrade, 1.0))))
                    .hasValue(validGrade);
        }

        @Test
        @DisplayName("a negative weight is rejected")
        void shouldRejectNegativeWeight() {
            assertThatThrownBy(() -> GradeAverageCalculator.weightedAverage(List.of(new WeightedGrade(5.0, -1.0))))
                    .isInstanceOf(IllegalArgumentException.class)
                    .hasMessageContaining("weight");
        }
    }

    @Nested
    @DisplayName("plain mean")
    class Mean {

        @ParameterizedTest(name = "mean of {0} is {1}")
        @CsvSource({
                "'5.0', 5.0",
                "'4.83;4.75', 4.79",
                "'5.17;4.79', 4.98",
                "'4.0;5.0;5.5', 4.83",
                "'5.125;5.0', 5.06"
        })
        void shouldAverageValuesUnweightedAndRoundToTwoDecimals(String values, double expected) {
            List<Double> list = Stream.of(values.split(";")).map(Double::valueOf).toList();

            assertThat(GradeAverageCalculator.mean(list)).hasValue(expected);
        }

        @Test
        @DisplayName("no values give no mean")
        void shouldBeEmptyWithoutValues() {
            assertThat(GradeAverageCalculator.mean(List.of())).isEmpty();
        }
    }

    @Nested
    @DisplayName("rounding")
    class Rounding {

        @ParameterizedTest(name = "{0} -> {1}")
        @CsvSource({
                "4.8333, 4.83",
                "5.1666, 5.17",
                "5.165, 5.17",
                "4.125, 4.13",
                "4.124, 4.12",
                "5.0, 5.0",
                "6.0, 6.0"
        })
        void shouldRoundHalfUpToTwoDecimals(double value, double expected) {
            assertThat(GradeAverageCalculator.roundToTwoDecimals(value)).isEqualTo(expected);
        }

        @ParameterizedTest(name = "Zeugnisnote of {0} is {1}")
        @CsvSource({
                "5.24, 5.0",
                "5.25, 5.5",
                "5.74, 5.5",
                "5.75, 6.0",
                "4.0, 4.0",
                "4.5, 4.5",
                "4.49, 4.5",
                "3.74, 3.5",
                "1.0, 1.0",
                "1.24, 1.0",
                "6.0, 6.0",
                "4.875, 5.0",
                "4.1, 4.0"
        })
        void shouldRoundToNearestSwissHalfGrade(double value, double expected) {
            assertThat(GradeAverageCalculator.roundToSwissHalf(value)).isEqualTo(expected);
        }
    }
}
