package ch.tbz.betterintranet.grades.api;

import ch.tbz.betterintranet.core.gateway.Query;

/** Asks for all grades of a user, grouped by school part and subject, with calculated averages. */
public record GetGradesOverviewQuery(String username) implements Query<GradesOverview> {
}
