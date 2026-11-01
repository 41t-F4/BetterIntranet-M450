package ch.tbz.betterintranet.grades.web;

import ch.tbz.betterintranet.core.gateway.QueryGateway;
import ch.tbz.betterintranet.core.web.CurrentUser;
import ch.tbz.betterintranet.grades.api.GradesOverview;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/** {@code GET /api/grades}: grades of the current user with averages. Thin, delegates to the query gateway. */
@RestController
@RequestMapping("/api/grades")
public class GradesController {

    private final QueryGateway queries;

    public GradesController(QueryGateway queries) {
        this.queries = queries;
    }

    @GetMapping
    public GradesOverview grades(CurrentUser user) {
        throw new UnsupportedOperationException("not implemented");
    }
}
