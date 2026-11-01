package ch.tbz.betterintranet.timetable.application;

import ch.tbz.betterintranet.core.gateway.QueryHandler;
import ch.tbz.betterintranet.timetable.api.GetClassesQuery;
import ch.tbz.betterintranet.timetable.api.SchoolClass;
import java.util.List;
import org.springframework.stereotype.Component;

@Component
public class GetClassesHandler implements QueryHandler<GetClassesQuery, List<SchoolClass>> {

    private final TimetableClient client;

    public GetClassesHandler(TimetableClient client) {
        this.client = client;
    }

    @Override
    public List<SchoolClass> handle(GetClassesQuery query) {
        throw new UnsupportedOperationException("not implemented");
    }
}
