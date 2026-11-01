package ch.tbz.betterintranet.timetable.application;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.verify;

import ch.tbz.betterintranet.timetable.api.GetHolidaysQuery;
import ch.tbz.betterintranet.timetable.api.Holiday;
import java.time.LocalDate;
import java.util.List;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class GetHolidaysHandlerTest {

    private static final Holiday KNABENSCHIESSEN =
            new Holiday("Knabenschiessen", LocalDate.of(2026, 9, 14), LocalDate.of(2026, 9, 14));
    private static final Holiday HERBSTFERIEN =
            new Holiday("Herbstferien", LocalDate.of(2026, 10, 5), LocalDate.of(2026, 10, 18));
    private static final Holiday LKB =
            new Holiday("Kantonale Lehrerkonferenz", LocalDate.of(2026, 11, 19), LocalDate.of(2026, 11, 19));
    private static final Holiday KLAUSUR =
            new Holiday("Klausurtagung IT", LocalDate.of(2026, 11, 19), LocalDate.of(2026, 11, 20));

    @Mock
    private TimetableClient client;

    @InjectMocks
    private GetHolidaysHandler handler;

    @Test
    @DisplayName("holidays come from the client sorted by start date")
    void sortsByStartDate() {
        given(client.fetchHolidays()).willReturn(List.of(LKB, HERBSTFERIEN, KNABENSCHIESSEN));

        List<Holiday> holidays = handler.handle(new GetHolidaysQuery());

        assertThat(holidays).containsExactly(KNABENSCHIESSEN, HERBSTFERIEN, LKB);
        verify(client).fetchHolidays();
    }

    @Test
    @DisplayName("holidays starting on the same day are ordered by name")
    void breaksTiesByName() {
        given(client.fetchHolidays()).willReturn(List.of(KLAUSUR, LKB));

        assertThat(handler.handle(new GetHolidaysQuery())).containsExactly(LKB, KLAUSUR);
    }
}
