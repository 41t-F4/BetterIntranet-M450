package ch.tbz.betterintranet.timetable.api;

import ch.tbz.betterintranet.core.gateway.Query;
import java.util.List;

/** All holidays of the current school year, sorted by start date. */
public record GetHolidaysQuery() implements Query<List<Holiday>> {
}
