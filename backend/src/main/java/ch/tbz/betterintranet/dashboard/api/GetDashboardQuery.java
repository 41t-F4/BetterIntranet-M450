package ch.tbz.betterintranet.dashboard.api;

import ch.tbz.betterintranet.core.gateway.Query;

/**
 * The home page content for one user.
 *
 * @param username login name of the user the dashboard is rendered for
 */
public record GetDashboardQuery(String username) implements Query<Dashboard> {

    public GetDashboardQuery {
        if (username == null || username.isBlank()) {
            throw new IllegalArgumentException("username must not be blank");
        }
    }
}
