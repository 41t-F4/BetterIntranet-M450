package ch.tbz.betterintranet.classes.application;

import ch.tbz.betterintranet.classes.api.GetMyClassesQuery;
import ch.tbz.betterintranet.classes.api.SchoolClassMembership;
import ch.tbz.betterintranet.core.gateway.QueryHandler;
import java.util.List;
import org.springframework.stereotype.Component;

/** Loads the user's classes from the school system and sorts them by school year (newest first) and short name. */
@Component
public class GetMyClassesHandler implements QueryHandler<GetMyClassesQuery, List<SchoolClassMembership>> {

    private final ClassesClient client;

    public GetMyClassesHandler(ClassesClient client) {
        this.client = client;
    }

    @Override
    public List<SchoolClassMembership> handle(GetMyClassesQuery query) {
        throw new UnsupportedOperationException("not implemented");
    }
}
