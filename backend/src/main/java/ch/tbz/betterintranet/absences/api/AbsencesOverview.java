package ch.tbz.betterintranet.absences.api;

import java.util.List;

/**
 * Result of {@link GetAbsencesQuery}.
 *
 * @param summary  numbers over all absences of the student, independent of any status filter
 * @param absences the (optionally filtered) entries, newest first
 */
public record AbsencesOverview(AbsenceSummary summary, List<Absence> absences) {
}
