package ch.tbz.betterintranet.dashboard.application;

import ch.tbz.betterintranet.core.gateway.QueryGateway;
import ch.tbz.betterintranet.core.gateway.QueryHandler;
import ch.tbz.betterintranet.dashboard.api.Dashboard;
import ch.tbz.betterintranet.dashboard.api.GetDashboardQuery;
import java.time.Clock;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Component;

/** Answers {@link GetDashboardQuery}. */
@Component
public class GetDashboardHandler implements QueryHandler<GetDashboardQuery, Dashboard> {

    private final DashboardContentRepository content;
    private final QueryGateway queries;
    private final Clock clock;

    /**
     * @param queries the gateway, injected lazily: the gateway collects all handlers at startup, so a handler
     *                that itself asks other modules would otherwise form a bean cycle
     */
    public GetDashboardHandler(DashboardContentRepository content, @Lazy QueryGateway queries, Clock clock) {
        this.content = content;
        this.queries = queries;
        this.clock = clock;
    }

    @Override
    public Dashboard handle(GetDashboardQuery query) {
        throw new UnsupportedOperationException("not implemented");
    }
}
