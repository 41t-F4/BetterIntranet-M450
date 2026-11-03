package ch.tbz.betterintranet.timetable.infrastructure;

import ch.tbz.betterintranet.core.error.UpstreamUnavailableException;
import ch.tbz.betterintranet.timetable.api.Holiday;
import ch.tbz.betterintranet.timetable.api.Lesson;
import ch.tbz.betterintranet.timetable.api.SchoolClass;
import ch.tbz.betterintranet.timetable.application.TimetableClient;
import java.time.Clock;
import java.time.LocalDate;
import java.util.Arrays;
import java.util.List;
import java.util.function.Supplier;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestClient;
import org.springframework.web.client.RestClientException;

/**
 * {@link TimetableClient} backed by {@code stundenplan.geekz.ch}. Every answer is cached for a few minutes (see
 * {@code spring.cache} in {@code application.yml}).
 *
 * <ul>
 *   <li>Network problems, 5xx answers, bodies that cannot be read and answers with an unexpected shape surface as
 *       {@link UpstreamUnavailableException} (HTTP 502).</li>
 *   <li>A 4xx answer means our request was wrong; it surfaces as {@link IllegalStateException} and becomes an
 *       internal error (HTTP 500), because retrying will not help.</li>
 *   <li>Only for the entries endpoint does 404 / {@code NOT_FOUND} mean «no lessons in this range».</li>
 * </ul>
 *
 * <p>The class list is requested for the week starting today (from the injected {@link Clock}). The upstream
 * returns all classes for any week, including holidays, so the range only has to be valid.
 */
@Component
public class GeekzTimetableClient implements TimetableClient {

    private static final String UPSTREAM = "timetable";
    private static final String FILTER_PATH = "/api/rest/view/v1/timetable/filter";
    private static final String ENTRIES_PATH = "/api/rest/view/v1/timetable/entries";
    private static final String HOLIDAYS_PATH = "/holidays";
    private static final String RESOURCE_TYPE_CLASS = "CLASS";
    private static final String TIMETABLE_TYPE_STANDARD = "STANDARD";
    private static final int CLASSES_LOOKAHEAD_DAYS = 6;

    private final RestClient restClient;
    private final GeekzTimetableMapper mapper = new GeekzTimetableMapper();

    private final Clock clock;

    public GeekzTimetableClient(@Qualifier("timetableRestClient") RestClient restClient, Clock clock) {
        this.restClient = restClient;
        this.clock = clock;
    }

    @Override
    @Cacheable("timetable-classes")
    public List<SchoolClass> fetchClasses() {
        LocalDate today = LocalDate.now(clock);
        GeekzClassesResponse response = call(FILTER_PATH, () -> restClient.get()
                .uri(uri -> uri.path(FILTER_PATH)
                        .queryParam("resourceType", RESOURCE_TYPE_CLASS)
                        .queryParam("timetableType", TIMETABLE_TYPE_STANDARD)
                        .queryParam("start", today)
                        .queryParam("end", today.plusDays(CLASSES_LOOKAHEAD_DAYS))
                        .build())
                .retrieve()
                .body(GeekzClassesResponse.class));
        return mapped(FILTER_PATH, () -> mapper.toSchoolClasses(response));
    }

    @Override
    @Cacheable("timetable-entries")
    public List<Lesson> fetchLessons(long classId, LocalDate from, LocalDate to) {
        GeekzEntriesResponse response = call(ENTRIES_PATH, () -> restClient.get()
                .uri(uri -> uri.path(ENTRIES_PATH)
                        .queryParam("start", from)
                        .queryParam("end", to)
                        .queryParam("format", 5)
                        .queryParam("resourceType", RESOURCE_TYPE_CLASS)
                        .queryParam("resources", classId)
                        .queryParam("periodTypes", "")
                        .queryParam("timetableType", TIMETABLE_TYPE_STANDARD)
                        .queryParam("layout", "START_TIME")
                        .build())
                .retrieve()
                .onStatus(status -> status == HttpStatus.NOT_FOUND, (request, answer) -> { })
                .body(GeekzEntriesResponse.class));
        return mapped(ENTRIES_PATH, () -> mapper.toLessons(response));
    }

    @Override
    @Cacheable("timetable-holidays")
    public List<Holiday> fetchHolidays() {
        GeekzHolidayResponse[] response = call(HOLIDAYS_PATH, () -> restClient.get()
                .uri(HOLIDAYS_PATH)
                .retrieve()
                .body(GeekzHolidayResponse[].class));
        return mapped(HOLIDAYS_PATH, () -> mapper.toHolidays(Arrays.asList(response)));
    }

    private static <T> T call(String path, Supplier<T> request) {
        T body;
        try {
            body = request.get();
        } catch (HttpClientErrorException ex) {
            throw new IllegalStateException("stundenplan.geekz.ch rejected GET " + path + " with "
                    + ex.getStatusCode().value() + ": " + ex.getResponseBodyAsString(), ex);
        } catch (RestClientException ex) {
            throw new UpstreamUnavailableException(UPSTREAM, "GET " + path + " failed: " + ex.getMessage(), ex);
        }
        if (body == null) {
            throw new UpstreamUnavailableException(UPSTREAM, "GET " + path + " returned an empty body");
        }
        return body;
    }

    /**
     * Runs the mapping of a received body. The mapper reports known contract violations itself; anything else it
     * trips over (a field of an unexpected type, a missing nested object) is equally an unusable answer.
     */
    private static <T> T mapped(String path, Supplier<T> mapping) {
        try {
            return mapping.get();
        } catch (UpstreamUnavailableException ex) {
            throw ex;
        } catch (RuntimeException ex) {
            throw new UpstreamUnavailableException(UPSTREAM, "GET " + path + " returned an unexpected shape", ex);
        }
    }
}
