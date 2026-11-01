package ch.tbz.betterintranet.timetable.web;

import static org.assertj.core.api.Assertions.assertThat;

import java.time.LocalDate;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

class WeekRangeTest {

    private static final LocalDate MONDAY = LocalDate.of(2026, 11, 2);
    private static final LocalDate FRIDAY = LocalDate.of(2026, 11, 6);

    @ParameterizedTest(name = "{0} lies in the week 2026-11-02..2026-11-06")
    @ValueSource(strings = {"2026-11-02", "2026-11-04", "2026-11-06", "2026-11-07", "2026-11-08"})
    void mondayToFridayOfIsoWeek(String day) {
        WeekRange week = WeekRange.containing(LocalDate.parse(day));

        assertThat(week.from()).isEqualTo(MONDAY);
        assertThat(week.to()).isEqualTo(FRIDAY);
    }

    @Test
    @DisplayName("a range may start on any day and then spans five days")
    void startingAtAnyDay() {
        WeekRange range = WeekRange.startingAt(LocalDate.of(2026, 11, 4));

        assertThat(range.from()).isEqualTo(LocalDate.of(2026, 11, 4));
        assertThat(range.to()).isEqualTo(LocalDate.of(2026, 11, 8));
    }

    @Test
    @DisplayName("a week never crosses a year boundary the wrong way")
    void yearBoundary() {
        WeekRange week = WeekRange.containing(LocalDate.of(2027, 1, 1));

        assertThat(week.from()).isEqualTo(LocalDate.of(2026, 12, 28));
        assertThat(week.to()).isEqualTo(LocalDate.of(2027, 1, 1));
    }
}
