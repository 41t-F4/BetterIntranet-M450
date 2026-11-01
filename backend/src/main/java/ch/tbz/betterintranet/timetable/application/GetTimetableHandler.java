package ch.tbz.betterintranet.timetable.application;

import ch.tbz.betterintranet.core.gateway.QueryHandler;
import ch.tbz.betterintranet.timetable.api.GetTimetableQuery;
import ch.tbz.betterintranet.timetable.api.Lesson;
import java.time.temporal.ChronoUnit;
import java.util.Comparator;
import java.util.List;
import org.springframework.stereotype.Component;

/**
 * Answers {@link GetTimetableQuery}: the lessons of one class, sorted by date and start time. Ranges longer
 * than {@value #MAX_RANGE_DAYS} days are rejected so that a single request cannot pull a whole year from the
 * upstream.
 */
@Component
public class GetTimetableHandler implements QueryHandler<GetTimetableQuery, List<Lesson>> {

    /** Longest accepted range in calendar days, both ends inclusive (roughly two months). */
    public static final int MAX_RANGE_DAYS = 62;

    private static final Comparator<Lesson> BY_DATE_THEN_START =
            Comparator.comparing(Lesson::date).thenComparing(Lesson::start);

    private final TimetableClient client;

    public GetTimetableHandler(TimetableClient client) {
        this.client = client;
    }

    @Override
    public List<Lesson> handle(GetTimetableQuery query) {
        long days = ChronoUnit.DAYS.between(query.from(), query.to()) + 1;
        if (days > MAX_RANGE_DAYS) {
            throw new IllegalArgumentException(
                    "The range must not be longer than " + MAX_RANGE_DAYS + " days, but was " + days);
        }
        return client.fetchLessons(query.classId(), query.from(), query.to()).stream()
                .sorted(BY_DATE_THEN_START)
                .toList();
    }
}
