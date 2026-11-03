package ch.tbz.betterintranet.timetable.web;

import ch.tbz.betterintranet.core.gateway.QueryGateway;
import ch.tbz.betterintranet.timetable.api.GetClassesQuery;
import ch.tbz.betterintranet.timetable.api.GetHolidaysQuery;
import ch.tbz.betterintranet.timetable.api.GetTimetableQuery;
import ch.tbz.betterintranet.timetable.api.Holiday;
import ch.tbz.betterintranet.timetable.api.Lesson;
import ch.tbz.betterintranet.timetable.api.SchoolClass;
import java.time.Clock;
import java.time.LocalDate;
import java.util.List;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/**
 * REST entry point of the timetable module. Thin by design: every request becomes a query on the
 * {@link QueryGateway}, which also enforces the module switch. Invalid ranges are rejected by
 * {@link GetTimetableQuery} and answered centrally by the core as HTTP 400.
 *
 * <pre>
 * GET /api/timetable/classes
 * GET /api/timetable?classId=7471&amp;from=2026-11-02&amp;to=2026-11-06
 * GET /api/timetable/holidays
 * </pre>
 */
@RestController
@RequestMapping("/api/timetable")
public class TimetableController {

    private final QueryGateway queries;
    private final Clock clock;

    public TimetableController(QueryGateway queries, Clock clock) {
        this.queries = queries;
        this.clock = clock;
    }

    @GetMapping("/classes")
    public List<SchoolClass> classes() {
        return queries.ask(new GetClassesQuery());
    }

    /**
     * Lessons of one class. Without {@code from} the Monday of the current school week is used, without
     * {@code to} the school week starting at {@code from} (i.e. Monday..Friday by default).
     */
    @GetMapping
    public List<Lesson> timetable(@RequestParam long classId,
                                  @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate from,
                                  @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate to) {
        LocalDate start = from != null ? from : WeekRange.containing(LocalDate.now(clock)).from();
        LocalDate end = to != null ? to : WeekRange.startingAt(start).to();
        return queries.ask(new GetTimetableQuery(classId, start, end));
    }

    @GetMapping("/holidays")
    public List<Holiday> holidays() {
        return queries.ask(new GetHolidaysQuery());
    }
}
