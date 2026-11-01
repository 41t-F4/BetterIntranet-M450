package ch.tbz.betterintranet.courses.api;

import ch.tbz.betterintranet.core.gateway.Query;
import java.util.List;

/**
 * All courses the given user attends, grouped by school part ({@code BMS}, {@code TBZ}, {@code UEK}) and
 * sorted by code. Optionally restricted to one semester.
 *
 * @param username the user whose courses are requested, e.g. {@code nicola.wehrli}
 * @param semester the semester to filter by, e.g. {@code HS 2026}; {@code null} means all semesters
 */
public record GetMyCoursesQuery(String username, String semester) implements Query<List<Course>> {

    /** Courses of all semesters. */
    public GetMyCoursesQuery(String username) {
        this(username, null);
    }

    /** {@code true} if the result should be restricted to {@link #semester()}. */
    public boolean hasSemester() {
        throw new UnsupportedOperationException("not implemented");
    }
}
