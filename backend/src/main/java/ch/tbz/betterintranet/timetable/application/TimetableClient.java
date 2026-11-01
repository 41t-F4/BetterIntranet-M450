package ch.tbz.betterintranet.timetable.application;

import ch.tbz.betterintranet.timetable.api.Holiday;
import ch.tbz.betterintranet.timetable.api.Lesson;
import ch.tbz.betterintranet.timetable.api.SchoolClass;
import java.time.LocalDate;
import java.util.List;

/**
 * Module-private port to the external timetable system. Implemented in {@code infrastructure}, mocked in
 * unit tests. Results come back in upstream order; the handlers take care of sorting.
 */
public interface TimetableClient {

    List<SchoolClass> fetchClasses();

    List<Lesson> fetchLessons(long classId, LocalDate from, LocalDate to);

    List<Holiday> fetchHolidays();
}
