package ch.tbz.betterintranet.timetable.application;

import ch.tbz.betterintranet.core.gateway.QueryHandler;
import ch.tbz.betterintranet.timetable.api.GetHolidaysQuery;
import ch.tbz.betterintranet.timetable.api.Holiday;
import java.util.Comparator;
import java.util.List;
import org.springframework.stereotype.Component;

/** Answers {@link GetHolidaysQuery}: all holidays and closed days, sorted by start date, then by name. */
@Component
public class GetHolidaysHandler implements QueryHandler<GetHolidaysQuery, List<Holiday>> {

    private static final Comparator<Holiday> BY_START_THEN_NAME =
            Comparator.comparing(Holiday::startDate).thenComparing(Holiday::name);

    private final TimetableClient client;

    public GetHolidaysHandler(TimetableClient client) {
        this.client = client;
    }

    @Override
    public List<Holiday> handle(GetHolidaysQuery query) {
        return client.fetchHolidays().stream().sorted(BY_START_THEN_NAME).toList();
    }
}
