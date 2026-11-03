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
import org.springframework.web.client.RestClient;
import org.springframework.web.client.RestClientException;

/**
 * {@link TimetableClient} backed by {@code stundenplan.geekz.ch}. Every answer is cached for a few minutes (see
 * {@code spring.cache} in {@code application.yml}). Network problems, 5xx answers and bodies that cannot be read
 * surface as {@link UpstreamUnavailableException}; a {@code NOT_FOUND} answer of the entries endpoint means that
 * there simply are no lessons.
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
        LocalDate today = LocalDate.now();
        GeekzClassesResponse response = call(FILTER_PATH, () -> restClient.get()
                .uri(uri -> uri.path(FILTER_PATH)
                        .queryParam("resourceType", RESOURCE_TYPE_CLASS)
                        .queryParam("timetableType", TIMETABLE_TYPE_STANDARD)
                        .queryParam("start", today)
                        .queryParam("end", today.plusDays(CLASSES_LOOKAHEAD_DAYS))
                        .build())
                .retrieve()
                .body(GeekzClassesResponse.class));
        return mapper.toSchoolClasses(response);
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
        return mapper.toLessons(response);
    }

    @Override
    @Cacheable("timetable-holidays")
    public List<Holiday> fetchHolidays() {
        GeekzHolidayResponse[] response = call(HOLIDAYS_PATH, () -> restClient.get()
                .uri(HOLIDAYS_PATH)
                .retrieve()
                .body(GeekzHolidayResponse[].class));
        return mapper.toHolidays(Arrays.asList(response));
    }

    private static <T> T call(String path, Supplier<T> request) {
        T body;
        try {
            body = request.get();
        } catch (RestClientException ex) {
            throw new UpstreamUnavailableException(UPSTREAM, "GET " + path + " failed: " + ex.getMessage(), ex);
        }
        if (body == null) {
            throw new UpstreamUnavailableException(UPSTREAM, "GET " + path + " returned an empty body");
        }
        return body;
    }
}
