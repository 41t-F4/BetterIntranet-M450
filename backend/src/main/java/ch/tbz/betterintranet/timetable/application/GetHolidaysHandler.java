package ch.tbz.betterintranet.timetable.application;

import ch.tbz.betterintranet.core.gateway.QueryHandler;
import ch.tbz.betterintranet.timetable.api.GetHolidaysQuery;
import ch.tbz.betterintranet.timetable.api.Holiday;
import java.util.List;
import org.springframework.stereotype.Component;

@Component
public class GetHolidaysHandler implements QueryHandler<GetHolidaysQuery, List<Holiday>> {

    private final TimetableClient client;

    public GetHolidaysHandler(TimetableClient client) {
        this.client = client;
    }

    @Override
    public List<Holiday> handle(GetHolidaysQuery query) {
        throw new UnsupportedOperationException("not implemented");
    }
}
