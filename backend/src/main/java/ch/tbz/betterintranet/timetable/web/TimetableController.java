package ch.tbz.betterintranet.timetable.web;

import ch.tbz.betterintranet.core.gateway.QueryGateway;
import ch.tbz.betterintranet.timetable.api.Holiday;
import ch.tbz.betterintranet.timetable.api.Lesson;
import ch.tbz.betterintranet.timetable.api.SchoolClass;
import java.time.LocalDate;
import java.util.List;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/timetable")
public class TimetableController {

    private final QueryGateway queries;

    public TimetableController(QueryGateway queries) {
        this.queries = queries;
    }

    @GetMapping("/classes")
    public List<SchoolClass> classes() {
        throw new UnsupportedOperationException("not implemented");
    }

    @GetMapping
    public List<Lesson> timetable(@RequestParam long classId,
                                  @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate from,
                                  @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate to) {
        throw new UnsupportedOperationException("not implemented");
    }

    @GetMapping("/holidays")
    public List<Holiday> holidays() {
        throw new UnsupportedOperationException("not implemented");
    }
}
