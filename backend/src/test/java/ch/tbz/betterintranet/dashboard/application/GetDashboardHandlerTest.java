package ch.tbz.betterintranet.dashboard.application;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.when;

import ch.tbz.betterintranet.core.error.HandlerNotFoundException;
import ch.tbz.betterintranet.core.error.ModuleDisabledException;
import ch.tbz.betterintranet.core.error.UpstreamUnavailableException;
import ch.tbz.betterintranet.core.gateway.QueryGateway;
import ch.tbz.betterintranet.dashboard.api.Dashboard;
import ch.tbz.betterintranet.dashboard.api.EventKind;
import ch.tbz.betterintranet.dashboard.api.GetDashboardQuery;
import ch.tbz.betterintranet.dashboard.api.Link;
import ch.tbz.betterintranet.dashboard.api.NewsItem;
import ch.tbz.betterintranet.dashboard.api.PreventionNotice;
import ch.tbz.betterintranet.dashboard.api.SchoolEvent;
import ch.tbz.betterintranet.timetable.api.GetHolidaysQuery;
import ch.tbz.betterintranet.timetable.api.Holiday;
import java.time.Clock;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.List;
import java.util.stream.IntStream;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class GetDashboardHandlerTest {

    private static final ZoneId ZURICH = ZoneId.of("Europe/Zurich");
    private static final LocalDate TODAY = LocalDate.of(2026, 11, 1);
    private static final Clock FIXED_CLOCK = Clock.fixed(TODAY.atTime(10, 0).atZone(ZURICH).toInstant(), ZURICH);
    private static final GetDashboardQuery QUERY = new GetDashboardQuery("nicola.wehrli");

    private static final PreventionNotice PREVENTION = new PreventionNotice("Prävention",
            "147.ch unterstützt junge Menschen bei Sorgen & Problemen.", "www.147.ch", "https://www.147.ch");
    private static final List<Link> MY_LINKS = List.of(new Link("Meine Kurse", "/klassen-kurse", false));
    private static final List<Link> QUICK_LINKS = List.of(new Link("OrgWeb", "https://orgweb.tbz.ch", true));

    @Mock
    private DashboardContentRepository content;

    @Mock
    private QueryGateway queries;

    private GetDashboardHandler handler;

    @BeforeEach
    void setUp() {
        handler = new GetDashboardHandler(content, queries, FIXED_CLOCK);
    }

    @Test
    @DisplayName("holidays from the timetable and own events are merged and sorted by start date")
    void mergesHolidaysAndEvents() {
        givenContent(List.of(), List.of(
                event("Prüfungswoche", "2027-01-18", "2027-01-22"),
                event("Elternabend", "2026-11-19", "2026-11-19")));
        givenHolidays(List.of(new Holiday("Weihnachtsferien", date("2026-12-21"), date("2027-01-02"))));

        Dashboard dashboard = handler.handle(QUERY);

        assertThat(dashboard.events()).containsExactly(
                new SchoolEvent("Elternabend", date("2026-11-19"), date("2026-11-19"), EventKind.EVENT),
                new SchoolEvent("Weihnachtsferien", date("2026-12-21"), date("2027-01-02"), EventKind.HOLIDAY),
                new SchoolEvent("Prüfungswoche", date("2027-01-18"), date("2027-01-22"), EventKind.EVENT));
    }

    @Test
    @DisplayName("events that ended before today are dropped, an event ending today is kept")
    void dropsPastEvents() {
        givenContent(List.of(), List.of(
                event("Gestern", "2026-10-31", "2026-10-31"),
                event("Endet heute", "2026-10-28", "2026-11-01"),
                event("Morgen", "2026-11-02", "2026-11-02")));
        givenHolidays(List.of(new Holiday("Herbstferien", date("2026-10-05"), date("2026-10-18"))));

        Dashboard dashboard = handler.handle(QUERY);

        assertThat(dashboard.events()).extracting(SchoolEvent::title).containsExactly("Endet heute", "Morgen");
    }

    @Test
    @DisplayName("at most the next eight events are shown")
    void limitsToEightEvents() {
        List<SchoolEvent> tenEvents = IntStream.rangeClosed(1, 10)
                .mapToObj(day -> event("Termin " + day, "2026-11-" + (day < 10 ? "0" + day : day),
                        "2026-11-" + (day < 10 ? "0" + day : day)))
                .toList();
        givenContent(List.of(), tenEvents);
        givenHolidays(List.of());

        Dashboard dashboard = handler.handle(QUERY);

        assertThat(dashboard.events()).hasSize(8)
                .extracting(SchoolEvent::title)
                .containsExactly("Termin 1", "Termin 2", "Termin 3", "Termin 4", "Termin 5", "Termin 6", "Termin 7",
                        "Termin 8");
    }

    @Test
    @DisplayName("news are sorted newest first and capped at five")
    void sortsNewsNewestFirstAndLimitsToFive() {
        givenContent(List.of(
                news(1, "2026-09-01"),
                news(2, "2026-10-28"),
                news(3, "2026-10-14"),
                news(4, "2026-10-21"),
                news(5, "2026-08-15"),
                news(6, "2026-10-07"),
                news(7, "2026-09-30")), List.of());
        givenHolidays(List.of());

        Dashboard dashboard = handler.handle(QUERY);

        assertThat(dashboard.news()).extracting(NewsItem::id).containsExactly(2L, 4L, 3L, 6L, 7L);
    }

    @Test
    @DisplayName("when the timetable module is disabled the dashboard shows only its own events")
    void degradesWhenTimetableModuleIsDisabled() {
        givenContent(List.of(), List.of(event("Elternabend", "2026-11-19", "2026-11-19")));
        when(queries.ask(new GetHolidaysQuery())).thenThrow(new ModuleDisabledException("timetable"));

        Dashboard dashboard = handler.handle(QUERY);

        assertThat(dashboard.events()).extracting(SchoolEvent::kind).containsOnly(EventKind.EVENT);
        assertThat(dashboard.events()).extracting(SchoolEvent::title).containsExactly("Elternabend");
    }

    @Test
    @DisplayName("when the timetable upstream is unavailable the dashboard shows only its own events")
    void degradesWhenTimetableIsUnavailable() {
        givenContent(List.of(), List.of(event("Elternabend", "2026-11-19", "2026-11-19")));
        when(queries.ask(new GetHolidaysQuery()))
                .thenThrow(new UpstreamUnavailableException("timetable", "connection refused"));

        Dashboard dashboard = handler.handle(QUERY);

        assertThat(dashboard.events()).extracting(SchoolEvent::title).containsExactly("Elternabend");
    }

    @Test
    @DisplayName("unexpected gateway errors are not swallowed")
    void propagatesUnexpectedErrors() {
        when(queries.ask(new GetHolidaysQuery())).thenThrow(new HandlerNotFoundException(GetHolidaysQuery.class));

        assertThatThrownBy(() -> handler.handle(QUERY)).isInstanceOf(HandlerNotFoundException.class);
    }

    @Test
    @DisplayName("prevention notice and links are passed through unchanged")
    void passesPreventionAndLinksThrough() {
        givenContent(List.of(), List.of());
        givenHolidays(List.of());

        Dashboard dashboard = handler.handle(QUERY);

        assertThat(dashboard.prevention()).isEqualTo(PREVENTION);
        assertThat(dashboard.myLinks()).isEqualTo(MY_LINKS);
        assertThat(dashboard.quickLinks()).isEqualTo(QUICK_LINKS);
    }

    private void givenContent(List<NewsItem> news, List<SchoolEvent> events) {
        when(content.news()).thenReturn(news);
        when(content.events()).thenReturn(events);
        when(content.prevention()).thenReturn(PREVENTION);
        when(content.myLinks()).thenReturn(MY_LINKS);
        when(content.quickLinks()).thenReturn(QUICK_LINKS);
    }

    private void givenHolidays(List<Holiday> holidays) {
        when(queries.ask(new GetHolidaysQuery())).thenReturn(holidays);
    }

    private static SchoolEvent event(String title, String start, String end) {
        return new SchoolEvent(title, date(start), date(end), EventKind.EVENT);
    }

    private static NewsItem news(long id, String publishedAt) {
        return new NewsItem(id, "News " + id, "Text " + id, date(publishedAt));
    }

    private static LocalDate date(String isoDate) {
        return LocalDate.parse(isoDate);
    }
}
