package ch.tbz.betterintranet.timetable.api;

import ch.tbz.betterintranet.core.gateway.Query;
import java.time.LocalDate;
import java.util.List;

/**
 * Lessons of one class between two dates (inclusive), sorted by date and start time.
 *
 * @param classId timetable resource id of the class
 * @param from    first day
 * @param to      last day, must not be before {@code from}
 */
public record GetTimetableQuery(long classId, LocalDate from, LocalDate to) implements Query<List<Lesson>> {

    public GetTimetableQuery {
        if (to.isBefore(from)) {
            throw new IllegalArgumentException("'to' must not be before 'from'");
        }
    }
}
