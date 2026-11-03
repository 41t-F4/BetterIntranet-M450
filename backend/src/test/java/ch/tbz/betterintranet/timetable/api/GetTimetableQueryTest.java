package ch.tbz.betterintranet.timetable.api;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import java.time.LocalDate;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class GetTimetableQueryTest {

    @Test
    @DisplayName("a range may be a single day")
    void acceptsSingleDay() {
        var day = LocalDate.of(2026, 11, 2);

        assertThat(new GetTimetableQuery(7471, day, day).from()).isEqualTo(day);
    }

    @Test
    @DisplayName("'to' before 'from' is rejected")
    void rejectsInvertedRange() {
        assertThatThrownBy(() -> new GetTimetableQuery(7471, LocalDate.of(2026, 11, 6), LocalDate.of(2026, 11, 2)))
                .isInstanceOf(IllegalArgumentException.class);
    }
}
