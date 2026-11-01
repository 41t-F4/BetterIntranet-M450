package ch.tbz.betterintranet.grades.application;

import ch.tbz.betterintranet.core.gateway.QueryHandler;
import ch.tbz.betterintranet.grades.api.GetGradesOverviewQuery;
import ch.tbz.betterintranet.grades.api.GradesOverview;
import org.springframework.stereotype.Component;

/** Loads the raw grades of a user and turns them into a grouped overview with averages. */
@Component
public class GetGradesOverviewHandler implements QueryHandler<GetGradesOverviewQuery, GradesOverview> {

    private final GradesClient client;

    public GetGradesOverviewHandler(GradesClient client) {
        this.client = client;
    }

    @Override
    public GradesOverview handle(GetGradesOverviewQuery query) {
        throw new UnsupportedOperationException("not implemented");
    }
}
