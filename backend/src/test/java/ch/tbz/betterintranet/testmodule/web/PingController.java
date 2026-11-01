package ch.tbz.betterintranet.testmodule.web;

import ch.tbz.betterintranet.core.error.NotFoundException;
import ch.tbz.betterintranet.core.error.UpstreamUnavailableException;
import ch.tbz.betterintranet.core.gateway.QueryGateway;
import ch.tbz.betterintranet.core.web.CurrentUser;
import ch.tbz.betterintranet.testmodule.api.PingQuery;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/test")
public class PingController {

    private final QueryGateway queries;

    public PingController(QueryGateway queries) {
        this.queries = queries;
    }

    @GetMapping("/ping")
    public String ping(CurrentUser user) {
        return queries.ask(new PingQuery(user.username()));
    }

    @GetMapping("/not-found")
    public String notFound() {
        throw new NotFoundException("nothing here");
    }

    @GetMapping("/upstream")
    public String upstream() {
        throw new UpstreamUnavailableException("timetable", "connection refused");
    }
}
