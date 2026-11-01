package ch.tbz.betterintranet.absences.web;

import ch.tbz.betterintranet.absences.api.AbsenceStatus;
import ch.tbz.betterintranet.absences.api.AbsencesOverview;
import ch.tbz.betterintranet.core.gateway.QueryGateway;
import ch.tbz.betterintranet.core.web.CurrentUser;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/** {@code GET /api/absences}: absences of the current user, optionally filtered by status. */
@RestController
@RequestMapping("/api/absences")
public class AbsencesController {

    private final QueryGateway queries;

    public AbsencesController(QueryGateway queries) {
        this.queries = queries;
    }

    @GetMapping
    public AbsencesOverview absences(CurrentUser user, @RequestParam(required = false) AbsenceStatus status) {
        throw new UnsupportedOperationException("not implemented");
    }
}
