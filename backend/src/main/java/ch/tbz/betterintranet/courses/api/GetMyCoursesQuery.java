package ch.tbz.betterintranet.courses.api;

import ch.tbz.betterintranet.core.gateway.Query;
import java.util.List;

/**
 * All courses the given user attends, newest semester first and then sorted by code. Optionally restricted to
 * one semester.
 *
 * @param username the user whose courses are requested, e.g. {@code nicola.wehrli}
 * @param semester the semester to filter by, e.g. {@code HS 2026}; {@code null} or blank means all semesters
 */
public record GetMyCoursesQuery(String username, String semester) implements Query<List<Course>> {

    public GetMyCoursesQuery {
        if (username == null || username.isBlank()) {
            throw new IllegalArgumentException("username must not be blank");
        }
        semester = semester == null || semester.isBlank() ? null : semester.trim();
    }

    /** Courses of all semesters. */
    public GetMyCoursesQuery(String username) {
        this(username, null);
    }

    /** {@code true} if the result should be restricted to {@link #semester()}. */
    public boolean hasSemester() {
        return semester != null;
    }
}
