package ch.tbz.betterintranet.timetable.application;

import ch.tbz.betterintranet.core.gateway.QueryHandler;
import ch.tbz.betterintranet.timetable.api.GetTimetableQuery;
import ch.tbz.betterintranet.timetable.api.Lesson;
import java.util.Comparator;
import java.util.List;
import org.springframework.stereotype.Component;

/**
 * Answers {@link GetTimetableQuery}: the lessons of one class, sorted by date and start time. The range has
 * already been validated by the query itself.
 */
@Component
public class GetTimetableHandler implements QueryHandler<GetTimetableQuery, List<Lesson>> {

    private static final Comparator<Lesson> BY_DATE_THEN_START =
            Comparator.comparing(Lesson::date).thenComparing(Lesson::start);

    private final TimetableClient client;

    public GetTimetableHandler(TimetableClient client) {
        this.client = client;
    }

    @Override
    public List<Lesson> handle(GetTimetableQuery query) {
        return client.fetchLessons(query.classId(), query.from(), query.to()).stream()
                .sorted(BY_DATE_THEN_START)
                .toList();
    }
}
