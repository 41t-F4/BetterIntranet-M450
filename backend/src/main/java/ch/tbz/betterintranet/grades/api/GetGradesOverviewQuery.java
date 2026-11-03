package ch.tbz.betterintranet.grades.api;

import ch.tbz.betterintranet.core.gateway.Query;

/**
 * Asks for all grades of a user, grouped by subject and semester, with calculated averages.
 *
 * @param username the user whose grades are requested, must not be blank
 */
public record GetGradesOverviewQuery(String username) implements Query<GradesOverview> {

    public GetGradesOverviewQuery {
        if (username == null || username.isBlank()) {
            throw new IllegalArgumentException("username must not be blank");
        }
    }
}
