package ch.tbz.betterintranet.classes.api;

import ch.tbz.betterintranet.core.gateway.Query;
import java.util.List;

/**
 * All classes the given user belongs to, newest school year first.
 *
 * @param username the user whose classes are requested, e.g. {@code nicola.wehrli}
 */
public record GetMyClassesQuery(String username) implements Query<List<SchoolClassMembership>> {

    public GetMyClassesQuery {
        if (username == null || username.isBlank()) {
            throw new IllegalArgumentException("username must not be blank");
        }
    }
}
