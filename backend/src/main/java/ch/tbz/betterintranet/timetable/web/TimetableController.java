package ch.tbz.betterintranet.timetable.web;

import ch.tbz.betterintranet.core.gateway.QueryGateway;
import ch.tbz.betterintranet.timetable.api.GetClassesQuery;
import ch.tbz.betterintranet.timetable.api.GetHolidaysQuery;
import ch.tbz.betterintranet.timetable.api.GetTimetableQuery;
import ch.tbz.betterintranet.timetable.api.Holiday;
import ch.tbz.betterintranet.timetable.api.Lesson;
import ch.tbz.betterintranet.timetable.api.SchoolClass;
import java.net.URI;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.List;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ProblemDetail;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/**
 * REST entry point of the timetable module. Thin by design: every request becomes a query on the
 * {@link QueryGateway}, which also enforces the module switch.
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

    private static final ZoneId SCHOOL_ZONE = ZoneId.of("Europe/Zurich");
    private static final int DEFAULT_RANGE_DAYS_AFTER_FROM = 4;

    private final QueryGateway queries;

    public TimetableController(QueryGateway queries) {
        this.queries = queries;
    }

    @GetMapping("/classes")
    public List<SchoolClass> classes() {
        return queries.ask(new GetClassesQuery());
    }

    /**
     * Lessons of one class. Without {@code from} the Monday of the current school week is used, without
     * {@code to} the fourth day after {@code from} (i.e. Friday for a Monday).
     */
    @GetMapping
    public List<Lesson> timetable(@RequestParam long classId,
                                  @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate from,
                                  @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate to) {
        LocalDate start = from != null ? from : WeekRange.containing(LocalDate.now(SCHOOL_ZONE)).from();
        LocalDate end = to != null ? to : start.plusDays(DEFAULT_RANGE_DAYS_AFTER_FROM);
        return queries.ask(new GetTimetableQuery(classId, start, end));
    }

    @GetMapping("/holidays")
    public List<Holiday> holidays() {
        return queries.ask(new GetHolidaysQuery());
    }

    /** Invalid ranges (inverted or too long) are client errors, not server errors. */
    @ExceptionHandler(IllegalArgumentException.class)
    public ProblemDetail invalidRequest(IllegalArgumentException ex) {
        ProblemDetail problem = ProblemDetail.forStatusAndDetail(HttpStatus.BAD_REQUEST, ex.getMessage());
        problem.setType(URI.create("urn:betterintranet:invalid-request"));
        problem.setTitle("Invalid request");
        return problem;
    }
}
