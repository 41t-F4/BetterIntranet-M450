package ch.tbz.betterintranet.timetable.api;

import ch.tbz.betterintranet.core.error.InvalidRequestException;
import ch.tbz.betterintranet.core.gateway.Query;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.Objects;

/**
 * Lessons of one class between two dates (inclusive), sorted by date and start time. The range is validated
 * here, so every caller gets the same rules and a violation is answered as HTTP 400 by the core.
 *
 * @param classId timetable resource id of the class
 * @param from    first day
 * @param to      last day, not before {@code from} and at most {@value #MAX_RANGE_DAYS} days after it (inclusive)
 * @throws InvalidRequestException if the range is inverted or longer than {@value #MAX_RANGE_DAYS} days
 */
public record GetTimetableQuery(long classId, LocalDate from, LocalDate to) implements Query<List<Lesson>> {

    /** Longest accepted range in calendar days, both ends inclusive (roughly two months). */
    public static final int MAX_RANGE_DAYS = 62;

    public GetTimetableQuery {
        Objects.requireNonNull(from, "from");
        Objects.requireNonNull(to, "to");
        if (to.isBefore(from)) {
            throw new InvalidRequestException("'to' must not be before 'from'");
        }
        long days = ChronoUnit.DAYS.between(from, to) + 1;
        if (days > MAX_RANGE_DAYS) {
            throw new InvalidRequestException(
                    "The range must not be longer than " + MAX_RANGE_DAYS + " days, but was " + days);
        }
    }
}
