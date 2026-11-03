package ch.tbz.betterintranet.dashboard.application;

import ch.tbz.betterintranet.core.error.ModuleDisabledException;
import ch.tbz.betterintranet.core.error.UpstreamUnavailableException;
import ch.tbz.betterintranet.core.gateway.QueryGateway;
import ch.tbz.betterintranet.core.gateway.QueryHandler;
import ch.tbz.betterintranet.dashboard.api.Dashboard;
import ch.tbz.betterintranet.dashboard.api.EventKind;
import ch.tbz.betterintranet.dashboard.api.GetDashboardQuery;
import ch.tbz.betterintranet.dashboard.api.NewsItem;
import ch.tbz.betterintranet.dashboard.api.SchoolEvent;
import ch.tbz.betterintranet.timetable.api.GetHolidaysQuery;
import ch.tbz.betterintranet.timetable.api.Holiday;
import java.time.Clock;
import java.time.LocalDate;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Stream;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Component;

/**
 * Answers {@link GetDashboardQuery}. "Termine der Schule" are the holidays from the timetable module plus
 * the dashboard's own events; when the timetable module is switched off or its upstream is down the
 * dashboard degrades to its own events instead of failing.
 */
@Component
public class GetDashboardHandler implements QueryHandler<GetDashboardQuery, Dashboard> {

    static final int MAX_NEWS = 5;
    static final int MAX_EVENTS = 8;

    private static final Logger log = LoggerFactory.getLogger(GetDashboardHandler.class);

    private static final Comparator<NewsItem> NEWEST_FIRST = Comparator
            .comparing(NewsItem::publishedAt).thenComparing(NewsItem::id).reversed();
    private static final Comparator<SchoolEvent> BY_START_DATE = Comparator
            .comparing(SchoolEvent::startDate).thenComparing(SchoolEvent::endDate).thenComparing(SchoolEvent::title);

    private final DashboardContentRepository content;
    private final QueryGateway queries;
    private final Clock clock;

    /**
     * @param queries the gateway, injected lazily: the gateway collects all handlers at startup, so a handler
     *                that itself asks other modules would otherwise form a bean cycle
     * @param clock   source of "today", fixed in tests
     */
    public GetDashboardHandler(DashboardContentRepository content, @Lazy QueryGateway queries, Clock clock) {
        this.content = content;
        this.queries = queries;
        this.clock = clock;
    }

    /**
     * The content is the same for every user for now; the query still names the user so that links and news
     * can be personalised later without changing the contract.
     */
    @Override
    public Dashboard handle(GetDashboardQuery query) {
        return new Dashboard(latestNews(), upcomingEvents(), content.prevention(), content.myLinks(),
                content.quickLinks());
    }

    private List<NewsItem> latestNews() {
        return content.news().stream()
                .sorted(NEWEST_FIRST)
                .limit(MAX_NEWS)
                .toList();
    }

    private List<SchoolEvent> upcomingEvents() {
        LocalDate today = LocalDate.now(clock);
        return Stream.concat(holidays().stream().map(GetDashboardHandler::toEvent), content.events().stream())
                .filter(event -> !event.endDate().isBefore(today))
                .sorted(BY_START_DATE)
                .limit(MAX_EVENTS)
                .toList();
    }

    private List<Holiday> holidays() {
        try {
            return queries.ask(new GetHolidaysQuery());
        } catch (ModuleDisabledException | UpstreamUnavailableException e) {
            log.warn("Holidays unavailable, dashboard shows its own events only: {}", e.getMessage());
            return List.of();
        }
    }

    private static SchoolEvent toEvent(Holiday holiday) {
        return new SchoolEvent(holiday.name(), holiday.startDate(), holiday.endDate(), EventKind.HOLIDAY);
    }
}
