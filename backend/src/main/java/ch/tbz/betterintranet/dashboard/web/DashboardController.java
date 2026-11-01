package ch.tbz.betterintranet.dashboard.web;

import ch.tbz.betterintranet.core.gateway.QueryGateway;
import ch.tbz.betterintranet.core.web.CurrentUser;
import ch.tbz.betterintranet.dashboard.api.Dashboard;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/** {@code GET /api/dashboard}: the home page content for the current user. */
@RestController
@RequestMapping("/api/dashboard")
public class DashboardController {

    private final QueryGateway queries;

    public DashboardController(QueryGateway queries) {
        this.queries = queries;
    }

    @GetMapping
    public Dashboard dashboard(CurrentUser user) {
        throw new UnsupportedOperationException("not implemented");
    }
}
