package ch.tbz.betterintranet.absences.application;

import ch.tbz.betterintranet.absences.api.Absence;
import ch.tbz.betterintranet.absences.api.AbsenceSummary;
import ch.tbz.betterintranet.absences.api.AbsencesOverview;
import ch.tbz.betterintranet.absences.api.GetAbsencesQuery;
import ch.tbz.betterintranet.absences.domain.AbsenceSummaryCalculator;
import ch.tbz.betterintranet.core.gateway.QueryHandler;
import java.util.Comparator;
import java.util.List;
import org.springframework.stereotype.Component;

/**
 * Loads the absences of a student, sorts them newest first and computes the summary. The summary always covers
 * all absences; the optional status filter of the query only narrows the returned list.
 */
@Component
public class GetAbsencesHandler implements QueryHandler<GetAbsencesQuery, AbsencesOverview> {

    private static final Comparator<Absence> NEWEST_FIRST = Comparator.comparing(Absence::date)
            .thenComparing(Absence::lessonStart)
            .reversed();

    private final AbsencesClient client;
    private final AbsenceSummaryCalculator calculator = new AbsenceSummaryCalculator();

    public GetAbsencesHandler(AbsencesClient client) {
        this.client = client;
    }

    @Override
    public AbsencesOverview handle(GetAbsencesQuery query) {
        List<Absence> all = client.fetchAbsences(query.username());
        AbsenceSummary summary = calculator.summarize(all);
        List<Absence> absences = all.stream()
                .filter(absence -> query.status() == null || absence.status() == query.status())
                .sorted(NEWEST_FIRST)
                .toList();
        return new AbsencesOverview(summary, absences);
    }
}
