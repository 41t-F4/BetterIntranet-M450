package ch.tbz.betterintranet.timetable.infrastructure;

import static com.github.tomakehurst.wiremock.client.WireMock.get;
import static com.github.tomakehurst.wiremock.client.WireMock.getRequestedFor;
import static com.github.tomakehurst.wiremock.client.WireMock.okJson;
import static com.github.tomakehurst.wiremock.client.WireMock.urlPathEqualTo;
import static com.github.tomakehurst.wiremock.core.WireMockConfiguration.wireMockConfig;
import static org.assertj.core.api.Assertions.assertThat;

import ch.tbz.betterintranet.timetable.TimetableFixtures;
import ch.tbz.betterintranet.timetable.api.Holiday;
import ch.tbz.betterintranet.timetable.api.Lesson;
import ch.tbz.betterintranet.timetable.application.TimetableClient;
import com.github.tomakehurst.wiremock.junit5.WireMockExtension;
import java.time.LocalDate;
import java.util.List;
import java.util.Objects;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.RegisterExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.cache.CacheManager;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;

/** Verifies that the Spring-wired client is cached, so repeated calls do not hit stundenplan.geekz.ch. */
@SpringBootTest
class TimetableCachingIT {

    private static final String ENTRIES = "/api/rest/view/v1/timetable/entries";
    private static final String FILTER = "/api/rest/view/v1/timetable/filter";
    private static final String HOLIDAYS = "/holidays";
    private static final long AP24B = 7471;
    private static final LocalDate MONDAY = LocalDate.of(2026, 11, 2);
    private static final LocalDate FRIDAY = LocalDate.of(2026, 11, 6);

    @RegisterExtension
    static WireMockExtension upstream = WireMockExtension.newInstance()
            .options(wireMockConfig().dynamicPort())
            .build();

    @DynamicPropertySource
    static void pointUpstreamToWireMock(DynamicPropertyRegistry registry) {
        registry.add("betterintranet.timetable.base-url", upstream::baseUrl);
    }

    @Autowired
    private TimetableClient client;

    @Autowired
    private CacheManager cacheManager;

    @BeforeEach
    void resetUpstreamAndCaches() {
        upstream.resetAll();
        cacheManager.getCacheNames().forEach(name -> Objects.requireNonNull(cacheManager.getCache(name)).clear());
        upstream.stubFor(get(urlPathEqualTo(HOLIDAYS))
                .willReturn(okJson(TimetableFixtures.read(TimetableFixtures.HOLIDAYS))));
        upstream.stubFor(get(urlPathEqualTo(FILTER))
                .willReturn(okJson(TimetableFixtures.read(TimetableFixtures.CLASSES))));
        upstream.stubFor(get(urlPathEqualTo(ENTRIES))
                .willReturn(okJson(TimetableFixtures.read(TimetableFixtures.ENTRIES_AP24B))));
    }

    @Test
    @DisplayName("two calls to fetchHolidays hit the upstream once")
    void holidaysAreCached() {
        List<Holiday> first = client.fetchHolidays();
        List<Holiday> second = client.fetchHolidays();

        assertThat(second).isEqualTo(first).isNotEmpty();
        upstream.verify(1, getRequestedFor(urlPathEqualTo(HOLIDAYS)));
    }

    @Test
    @DisplayName("two calls to fetchClasses hit the upstream once")
    void classesAreCached() {
        client.fetchClasses();
        client.fetchClasses();

        upstream.verify(1, getRequestedFor(urlPathEqualTo(FILTER)));
    }

    @Test
    @DisplayName("lessons are cached per class and range")
    void lessonsAreCachedPerClassAndRange() {
        List<Lesson> first = client.fetchLessons(AP24B, MONDAY, FRIDAY);
        List<Lesson> second = client.fetchLessons(AP24B, MONDAY, FRIDAY);
        client.fetchLessons(AP24B, MONDAY.plusWeeks(1), FRIDAY.plusWeeks(1));

        assertThat(second).isEqualTo(first).hasSize(4);
        upstream.verify(2, getRequestedFor(urlPathEqualTo(ENTRIES)));
    }
}
