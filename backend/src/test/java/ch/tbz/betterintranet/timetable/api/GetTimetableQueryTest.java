package ch.tbz.betterintranet.timetable.api;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import ch.tbz.betterintranet.core.error.InvalidRequestException;
import java.time.LocalDate;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class GetTimetableQueryTest {

    private static final LocalDate MONDAY = LocalDate.of(2026, 11, 2);

    @Test
    @DisplayName("a range may be a single day")
    void acceptsSingleDay() {
        assertThat(new GetTimetableQuery(7471, MONDAY, MONDAY).from()).isEqualTo(MONDAY);
    }

    @Test
    @DisplayName("a range of exactly 62 days is accepted")
    void acceptsMaximumRange() {
        assertThat(new GetTimetableQuery(7471, MONDAY, MONDAY.plusDays(61)).to()).isEqualTo(MONDAY.plusDays(61));
    }

    @Test
    @DisplayName("'to' before 'from' is an invalid request")
    void rejectsInvertedRange() {
        assertThatThrownBy(() -> new GetTimetableQuery(7471, MONDAY.plusDays(4), MONDAY))
                .isInstanceOf(InvalidRequestException.class)
                .hasMessage("'to' must not be before 'from'");
    }

    @Test
    @DisplayName("a range longer than 62 days is an invalid request")
    void rejectsTooLongRange() {
        assertThatThrownBy(() -> new GetTimetableQuery(7471, MONDAY, MONDAY.plusDays(62)))
                .isInstanceOf(InvalidRequestException.class)
                .hasMessage("The range must not be longer than 62 days, but was 63");
    }
}
