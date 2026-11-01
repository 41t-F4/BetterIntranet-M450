package ch.tbz.betterintranet.absences.application;

import ch.tbz.betterintranet.absences.api.AbsencesOverview;
import ch.tbz.betterintranet.absences.api.GetAbsencesQuery;
import ch.tbz.betterintranet.core.gateway.QueryHandler;
import org.springframework.stereotype.Component;

/** Loads the absences of a student, sorts them newest first and computes the summary. */
@Component
public class GetAbsencesHandler implements QueryHandler<GetAbsencesQuery, AbsencesOverview> {

    private final AbsencesClient client;

    public GetAbsencesHandler(AbsencesClient client) {
        this.client = client;
    }

    @Override
    public AbsencesOverview handle(GetAbsencesQuery query) {
        throw new UnsupportedOperationException("not implemented");
    }
}
