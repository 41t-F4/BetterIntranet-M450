package ch.tbz.betterintranet.timetable.application;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.verify;

import ch.tbz.betterintranet.timetable.api.GetTimetableQuery;
import ch.tbz.betterintranet.timetable.api.Lesson;
import ch.tbz.betterintranet.timetable.api.LessonStatus;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class GetTimetableHandlerTest {

    private static final long AP24B = 7471;
    private static final LocalDate MONDAY = LocalDate.of(2026, 11, 2);
    private static final LocalDate FRIDAY = LocalDate.of(2026, 11, 6);

    @Mock
    private TimetableClient client;

    @InjectMocks
    private GetTimetableHandler handler;

    @Test
    @DisplayName("the query is delegated to the client with class id and range")
    void delegatesToClient() {
        Lesson lesson = lesson(MONDAY, 8, 10);
        given(client.fetchLessons(AP24B, MONDAY, FRIDAY)).willReturn(List.of(lesson));

        List<Lesson> lessons = handler.handle(new GetTimetableQuery(AP24B, MONDAY, FRIDAY));

        assertThat(lessons).containsExactly(lesson);
        verify(client).fetchLessons(AP24B, MONDAY, FRIDAY);
    }

    @Test
    @DisplayName("lessons are sorted by date, then by start time")
    void sortsByDateThenStart() {
        Lesson tuesdayLate = lesson(MONDAY.plusDays(1), 13, 25);
        Lesson tuesdayEarly = lesson(MONDAY.plusDays(1), 8, 10);
        Lesson mondayLate = lesson(MONDAY, 15, 20);
        given(client.fetchLessons(AP24B, MONDAY, FRIDAY)).willReturn(List.of(tuesdayLate, mondayLate, tuesdayEarly));

        List<Lesson> lessons = handler.handle(new GetTimetableQuery(AP24B, MONDAY, FRIDAY));

        assertThat(lessons).containsExactly(mondayLate, tuesdayEarly, tuesdayLate);
    }

    private static Lesson lesson(LocalDate date, int hour, int minute) {
        LocalTime start = LocalTime.of(hour, minute);
        return new Lesson(date, start, start.plusMinutes(95), "ENG", "Englisch", List.of("MID"), List.of("AU-512"),
                LessonStatus.REGULAR, null);
    }
}
