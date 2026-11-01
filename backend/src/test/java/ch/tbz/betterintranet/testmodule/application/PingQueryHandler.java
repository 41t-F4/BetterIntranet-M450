package ch.tbz.betterintranet.testmodule.application;

import ch.tbz.betterintranet.core.gateway.QueryHandler;
import ch.tbz.betterintranet.testmodule.api.PingQuery;
import org.springframework.stereotype.Component;

@Component
public class PingQueryHandler implements QueryHandler<PingQuery, String> {

    @Override
    public String handle(PingQuery query) {
        return "pong:" + query.echo();
    }
}
