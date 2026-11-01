package ch.tbz.betterintranet.timetable.application;

import ch.tbz.betterintranet.core.gateway.QueryHandler;
import ch.tbz.betterintranet.timetable.api.GetClassesQuery;
import ch.tbz.betterintranet.timetable.api.SchoolClass;
import java.util.Comparator;
import java.util.List;
import org.springframework.stereotype.Component;

/** Answers {@link GetClassesQuery}: all classes of the timetable system, sorted by short name. */
@Component
public class GetClassesHandler implements QueryHandler<GetClassesQuery, List<SchoolClass>> {

    private static final Comparator<SchoolClass> BY_SHORT_NAME =
            Comparator.comparing(SchoolClass::shortName, String.CASE_INSENSITIVE_ORDER)
                    .thenComparing(SchoolClass::id);

    private final TimetableClient client;

    public GetClassesHandler(TimetableClient client) {
        this.client = client;
    }

    @Override
    public List<SchoolClass> handle(GetClassesQuery query) {
        return client.fetchClasses().stream().sorted(BY_SHORT_NAME).toList();
    }
}
