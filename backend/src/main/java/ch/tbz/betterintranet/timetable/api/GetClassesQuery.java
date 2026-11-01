package ch.tbz.betterintranet.timetable.api;

import ch.tbz.betterintranet.core.gateway.Query;
import java.util.List;

/** All classes known to the timetable system, sorted by short name. */
public record GetClassesQuery() implements Query<List<SchoolClass>> {
}
