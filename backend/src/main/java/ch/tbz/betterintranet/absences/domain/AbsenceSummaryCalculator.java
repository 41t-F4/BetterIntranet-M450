package ch.tbz.betterintranet.absences.domain;

import ch.tbz.betterintranet.absences.api.Absence;
import ch.tbz.betterintranet.absences.api.AbsenceSummary;
import java.util.List;

/** Derives the {@link AbsenceSummary} (counts per status, missed lessons) from a list of absences. */
public class AbsenceSummaryCalculator {

    public AbsenceSummary summarize(List<Absence> absences) {
        throw new UnsupportedOperationException("not implemented");
    }
}
