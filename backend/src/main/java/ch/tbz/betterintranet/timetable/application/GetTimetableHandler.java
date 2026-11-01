package ch.tbz.betterintranet.timetable.application;

import ch.tbz.betterintranet.core.gateway.QueryHandler;
import ch.tbz.betterintranet.timetable.api.GetTimetableQuery;
import ch.tbz.betterintranet.timetable.api.Lesson;
import java.util.List;
import org.springframework.stereotype.Component;

@Component
public class GetTimetableHandler implements QueryHandler<GetTimetableQuery, List<Lesson>> {

    private final TimetableClient client;

    public GetTimetableHandler(TimetableClient client) {
        this.client = client;
    }

    @Override
    public List<Lesson> handle(GetTimetableQuery query) {
        throw new UnsupportedOperationException("not implemented");
    }
}
