package ch.tbz.betterintranet.testmodule.web;

import ch.tbz.betterintranet.core.error.InvalidRequestException;
import ch.tbz.betterintranet.core.error.NotFoundException;
import ch.tbz.betterintranet.core.error.UpstreamUnavailableException;
import ch.tbz.betterintranet.core.gateway.QueryGateway;
import ch.tbz.betterintranet.core.web.CurrentUser;
import ch.tbz.betterintranet.testmodule.api.PingQuery;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
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

    @GetMapping("/invalid")
    public String invalid() {
        throw new InvalidRequestException("The range must not be longer than 62 days.");
    }

    @GetMapping("/boom")
    public String boom() {
        throw new IllegalStateException("secret internal state at line 42");
    }

    @GetMapping("/number")
    public long number(@RequestParam long value) {
        return value;
    }
}
