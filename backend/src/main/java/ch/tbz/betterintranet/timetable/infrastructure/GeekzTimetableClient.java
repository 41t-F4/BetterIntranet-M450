package ch.tbz.betterintranet.timetable.infrastructure;

import ch.tbz.betterintranet.timetable.api.Holiday;
import ch.tbz.betterintranet.timetable.api.Lesson;
import ch.tbz.betterintranet.timetable.api.SchoolClass;
import ch.tbz.betterintranet.timetable.application.TimetableClient;
import java.time.LocalDate;
import java.util.List;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;

/** {@link TimetableClient} backed by {@code stundenplan.geekz.ch}. */
@Component
public class GeekzTimetableClient implements TimetableClient {

    private final RestClient restClient;

    public GeekzTimetableClient(@Qualifier("timetableRestClient") RestClient restClient) {
        this.restClient = restClient;
    }

    @Override
    public List<SchoolClass> fetchClasses() {
        throw new UnsupportedOperationException("not implemented");
    }

    @Override
    public List<Lesson> fetchLessons(long classId, LocalDate from, LocalDate to) {
        throw new UnsupportedOperationException("not implemented");
    }

    @Override
    public List<Holiday> fetchHolidays() {
        throw new UnsupportedOperationException("not implemented");
    }
}
