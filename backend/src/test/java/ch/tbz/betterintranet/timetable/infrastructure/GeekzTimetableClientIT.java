package ch.tbz.betterintranet.timetable.infrastructure;

import static com.github.tomakehurst.wiremock.client.WireMock.aResponse;
import static com.github.tomakehurst.wiremock.client.WireMock.equalTo;
import static com.github.tomakehurst.wiremock.client.WireMock.get;
import static com.github.tomakehurst.wiremock.client.WireMock.getRequestedFor;
import static com.github.tomakehurst.wiremock.client.WireMock.matching;
import static com.github.tomakehurst.wiremock.client.WireMock.okJson;
import static com.github.tomakehurst.wiremock.client.WireMock.serverError;
import static com.github.tomakehurst.wiremock.client.WireMock.urlPathEqualTo;
import static com.github.tomakehurst.wiremock.core.WireMockConfiguration.wireMockConfig;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import ch.tbz.betterintranet.core.error.UpstreamUnavailableException;
import ch.tbz.betterintranet.timetable.TimetableFixtures;
import ch.tbz.betterintranet.timetable.api.Holiday;
import ch.tbz.betterintranet.timetable.api.Lesson;
import ch.tbz.betterintranet.timetable.api.LessonStatus;
import ch.tbz.betterintranet.timetable.api.SchoolClass;
import com.github.tomakehurst.wiremock.junit5.WireMockExtension;
import java.io.IOException;
import java.net.ServerSocket;
import java.time.Clock;
import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.ZoneId;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.RegisterExtension;
import org.springframework.web.client.RestClient;

/** Drives the real HTTP client against a WireMock stand-in for stundenplan.geekz.ch. No Spring context needed. */
class GeekzTimetableClientIT {

    private static final String ENTRIES = "/api/rest/view/v1/timetable/entries";
    private static final String FILTER = "/api/rest/view/v1/timetable/filter";
    private static final String HOLIDAYS = "/holidays";
    private static final String ISO_DATE = "\\d{4}-\\d{2}-\\d{2}";
    private static final long AP24B = 7471;
    private static final LocalDate MONDAY = LocalDate.of(2026, 11, 2);
    private static final LocalDate FRIDAY = LocalDate.of(2026, 11, 6);
    /** Wednesday of the autumn holidays: the class list must still be requested for «today». */
    private static final Clock IN_AUTUMN_HOLIDAYS =
            Clock.fixed(Instant.parse("2026-10-07T08:00:00Z"), ZoneId.of("Europe/Zurich"));

    @RegisterExtension
    static WireMockExtension upstream = WireMockExtension.newInstance()
            .options(wireMockConfig().dynamicPort())
            .build();

    private GeekzTimetableClient client;

    @BeforeEach
    void createClient() {
        upstream.resetAll();
        client = new GeekzTimetableClient(RestClient.builder().baseUrl(upstream.baseUrl()).build(), IN_AUTUMN_HOLIDAYS);
    }

    @Nested
    class Classes {

        @Test
        @DisplayName("fetchClasses asks the filter endpoint for CLASS resources and maps every class")
        void fetchesClasses() {
            upstream.stubFor(get(urlPathEqualTo(FILTER))
                    .withQueryParam("resourceType", equalTo("CLASS"))
                    .withQueryParam("timetableType", equalTo("STANDARD"))
                    .withQueryParam("start", equalTo("2026-10-07"))
                    .withQueryParam("end", equalTo("2026-10-13"))
                    .willReturn(okJson(TimetableFixtures.read(TimetableFixtures.CLASSES))));

            List<SchoolClass> classes = client.fetchClasses();

            assertThat(classes).hasSize(11)
                    .contains(new SchoolClass(AP24B, "AP 24b", "Informatiker/innen Applikationsentwicklung", "IT"));
        }
    }

    @Nested
    class Lessons {

        @Test
        @DisplayName("fetchLessons passes class id and range as the upstream expects them")
        void fetchesLessons() {
            upstream.stubFor(get(urlPathEqualTo(ENTRIES))
                    .willReturn(okJson(TimetableFixtures.read(TimetableFixtures.ENTRIES_AP24B))));

            List<Lesson> lessons = client.fetchLessons(AP24B, MONDAY, FRIDAY);

            assertThat(lessons).hasSize(4);
            assertThat(lessons.getFirst()).isEqualTo(new Lesson(LocalDate.of(2026, 11, 3), LocalTime.of(8, 10),
                    LocalTime.of(9, 45), "ENG", "Englisch", List.of("MID"), List.of("AU-512"), LessonStatus.REGULAR, "oB"));
            upstream.verify(getRequestedFor(urlPathEqualTo(ENTRIES))
                    .withQueryParam("start", equalTo("2026-11-02"))
                    .withQueryParam("end", equalTo("2026-11-06"))
                    .withQueryParam("format", equalTo("5"))
                    .withQueryParam("resourceType", equalTo("CLASS"))
                    .withQueryParam("resources", equalTo("7471"))
                    .withQueryParam("periodTypes", equalTo(""))
                    .withQueryParam("timetableType", equalTo("STANDARD"))
                    .withQueryParam("layout", equalTo("START_TIME")));
        }

        @Test
        @DisplayName("a NOT_FOUND answer means no lessons, not an error")
        void notFoundBodyIsEmpty() {
            upstream.stubFor(get(urlPathEqualTo(ENTRIES))
                    .willReturn(okJson("{\"errorCode\":\"NOT_FOUND\",\"message\":\"no timetable\"}")));

            assertThat(client.fetchLessons(AP24B, MONDAY, FRIDAY)).isEmpty();
        }

        @Test
        @DisplayName("an HTTP 404 with a NOT_FOUND body also means no lessons")
        void notFoundStatusIsEmpty() {
            upstream.stubFor(get(urlPathEqualTo(ENTRIES))
                    .willReturn(aResponse().withStatus(404)
                            .withHeader("Content-Type", "application/json")
                            .withBody("{\"errorCode\":\"NOT_FOUND\"}")));

            assertThat(client.fetchLessons(AP24B, MONDAY, FRIDAY)).isEmpty();
        }
    }

    @Nested
    class Holidays {

        @Test
        @DisplayName("fetchHolidays maps the holiday array")
        void fetchesHolidays() {
            upstream.stubFor(get(urlPathEqualTo(HOLIDAYS))
                    .willReturn(okJson(TimetableFixtures.read(TimetableFixtures.HOLIDAYS))));

            List<Holiday> holidays = client.fetchHolidays();

            assertThat(holidays).hasSize(15)
                    .contains(new Holiday("Herbstferien", LocalDate.of(2026, 10, 5), LocalDate.of(2026, 10, 18)));
        }
    }

    @Nested
    class Failures {

        @Test
        @DisplayName("a 4xx answer is our fault: an internal error, not «upstream unavailable»")
        void clientErrorIsNotUnavailable() {
            upstream.stubFor(get(urlPathEqualTo(ENTRIES)).willReturn(aResponse().withStatus(400)
                    .withHeader("Content-Type", "application/json")
                    .withBody("{\"errorCode\":\"BAD_REQUEST\",\"errorMessage\":\"no valid value\"}")));

            assertThatThrownBy(() -> client.fetchLessons(AP24B, MONDAY, FRIDAY))
                    .isInstanceOf(IllegalStateException.class)
                    .isNotInstanceOf(UpstreamUnavailableException.class)
                    .hasMessageContaining("400");
        }

        @Test
        @DisplayName("a 404 is only «no lessons» for the entries endpoint; elsewhere it is an internal error")
        void notFoundOnOtherEndpointsIsNotEmpty() {
            upstream.stubFor(get(urlPathEqualTo(HOLIDAYS)).willReturn(aResponse().withStatus(404)));

            assertThatThrownBy(() -> client.fetchHolidays())
                    .isInstanceOf(IllegalStateException.class)
                    .hasMessageContaining("404");
        }

        @Test
        @DisplayName("an errorCode other than NOT_FOUND in a 200 body is reported as unavailable")
        void otherErrorCodeIsUpstreamUnavailable() {
            upstream.stubFor(get(urlPathEqualTo(ENTRIES))
                    .willReturn(okJson("{\"errorCode\":\"INTERNAL_ERROR\"}")));

            assertThatThrownBy(() -> client.fetchLessons(AP24B, MONDAY, FRIDAY))
                    .isInstanceOf(UpstreamUnavailableException.class)
                    .hasMessageContaining("INTERNAL_ERROR");
        }

        @Test
        @DisplayName("a lesson without duration is reported as unavailable instead of a raw 500")
        void incompleteLessonIsUpstreamUnavailable() {
            upstream.stubFor(get(urlPathEqualTo(ENTRIES)).willReturn(okJson(
                    "{\"days\":[{\"date\":\"2026-11-03\",\"status\":\"REGULAR\",\"gridEntries\":[{\"status\":\"REGULAR\"}]}]}")));

            assertThatThrownBy(() -> client.fetchLessons(AP24B, MONDAY, FRIDAY))
                    .isInstanceOf(UpstreamUnavailableException.class);
        }

        @Test
        @DisplayName("a 5xx answer is reported as upstream unavailable")
        void serverErrorIsUpstreamUnavailable() {
            upstream.stubFor(get(urlPathEqualTo(HOLIDAYS)).willReturn(serverError()));

            assertThatThrownBy(() -> client.fetchHolidays())
                    .isInstanceOfSatisfying(UpstreamUnavailableException.class,
                            ex -> assertThat(ex.upstream()).isEqualTo("timetable"));
        }

        @Test
        @DisplayName("a malformed answer is reported as upstream unavailable")
        void malformedBodyIsUpstreamUnavailable() {
            upstream.stubFor(get(urlPathEqualTo(ENTRIES))
                    .willReturn(aResponse().withStatus(200).withHeader("Content-Type", "text/html")
                            .withBody("<html>maintenance</html>")));

            assertThatThrownBy(() -> client.fetchLessons(AP24B, MONDAY, FRIDAY))
                    .isInstanceOf(UpstreamUnavailableException.class);
        }

        @Test
        @DisplayName("an empty answer is reported as upstream unavailable")
        void emptyBodyIsUpstreamUnavailable() {
            upstream.stubFor(get(urlPathEqualTo(FILTER)).willReturn(aResponse().withStatus(200)));

            assertThatThrownBy(() -> client.fetchClasses())
                    .isInstanceOf(UpstreamUnavailableException.class);
        }

        @Test
        @DisplayName("an unreachable upstream is reported as upstream unavailable")
        void connectionRefusedIsUpstreamUnavailable() throws IOException {
            int unusedPort;
            try (ServerSocket socket = new ServerSocket(0)) {
                unusedPort = socket.getLocalPort();
            }
            GeekzTimetableClient unreachable = new GeekzTimetableClient(
                    RestClient.builder().baseUrl("http://localhost:" + unusedPort).build(), IN_AUTUMN_HOLIDAYS);

            assertThatThrownBy(unreachable::fetchHolidays)
                    .isInstanceOfSatisfying(UpstreamUnavailableException.class,
                            ex -> assertThat(ex.upstream()).isEqualTo("timetable"));
        }
    }
}
