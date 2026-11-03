package ch.tbz.betterintranet.user.api;

import ch.tbz.betterintranet.core.gateway.Query;

/**
 * Profile of the user with the given username.
 *
 * @param username login name as sent in the {@code X-User} header, e.g. {@code nicola.wehrli}
 */
public record GetUserProfileQuery(String username) implements Query<UserProfile> {

    public GetUserProfileQuery {
        if (username == null || username.isBlank()) {
            throw new IllegalArgumentException("username must not be blank");
        }
    }
}
