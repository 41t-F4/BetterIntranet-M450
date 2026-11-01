package ch.tbz.betterintranet.classes.application;

import ch.tbz.betterintranet.classes.api.GetMyClassesQuery;
import ch.tbz.betterintranet.classes.api.SchoolClassMembership;
import ch.tbz.betterintranet.core.gateway.QueryHandler;
import java.util.Comparator;
import java.util.List;
import org.springframework.stereotype.Component;

/** Loads the user's classes from the school system and sorts them by school year (newest first) and short name. */
@Component
public class GetMyClassesHandler implements QueryHandler<GetMyClassesQuery, List<SchoolClassMembership>> {

    private static final Comparator<SchoolClassMembership> NEWEST_SCHOOL_YEAR_THEN_SHORT_NAME =
            Comparator.comparing(SchoolClassMembership::schoolYear, Comparator.reverseOrder())
                    .thenComparing(SchoolClassMembership::shortName);

    private final ClassesClient client;

    public GetMyClassesHandler(ClassesClient client) {
        this.client = client;
    }

    @Override
    public List<SchoolClassMembership> handle(GetMyClassesQuery query) {
        return client.classesOf(query.username()).stream()
                .sorted(NEWEST_SCHOOL_YEAR_THEN_SHORT_NAME)
                .toList();
    }
}
