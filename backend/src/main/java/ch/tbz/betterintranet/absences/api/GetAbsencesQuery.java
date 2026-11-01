package ch.tbz.betterintranet.absences.api;

import ch.tbz.betterintranet.core.gateway.Query;

/**
 * Absences of one student, newest first, together with a summary over all of them.
 *
 * @param username the student's username, must not be blank
 * @param status   only return entries with this status; {@code null} returns all
 */
public record GetAbsencesQuery(String username, AbsenceStatus status) implements Query<AbsencesOverview> {

    public GetAbsencesQuery(String username) {
        this(username, null);
    }
}
