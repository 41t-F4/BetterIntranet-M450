package ch.tbz.betterintranet.dashboard.web;

import static org.hamcrest.Matchers.everyItem;
import static org.mockito.Mockito.when;
import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.not;
import static org.hamcrest.Matchers.hasItem;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import ch.tbz.betterintranet.core.error.UpstreamUnavailableException;
import ch.tbz.betterintranet.timetable.api.Holiday;
import ch.tbz.betterintranet.timetable.application.TimetableClient;
import java.time.Clock;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Import;
import org.springframework.context.annotation.Primary;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

/**
 * {@code GET /api/dashboard} through controller, gateway, handler and the JSON-backed repository. The
 * timetable module runs for real, only its upstream port {@link TimetableClient} is mocked; the clock is fixed to 2026-11-01 so the
 * shipped mock events are all upcoming.
 */
class DashboardControllerIT {

    private static final ZoneId ZURICH = ZoneId.of("Europe/Zurich");
    private static final LocalDate TODAY = LocalDate.of(2026, 11, 1);

    @TestConfiguration
    static class TimetableStub {

        @Bean
        @Primary
        Clock fixedClock() {
            return Clock.fixed(TODAY.atTime(8, 0).atZone(ZURICH).toInstant(), ZURICH);
        }
    }

    @Nested
    @SpringBootTest
    @AutoConfigureMockMvc
    @Import(TimetableStub.class)
    class ModuleEnabled {

        @Autowired
        private MockMvc mockMvc;

        @MockitoBean
        private TimetableClient timetableClient;

        @BeforeEach
        void holidaysFromTimetable() {
            when(timetableClient.fetchHolidays()).thenReturn(List.of(
                    new Holiday("Herbstferien", LocalDate.of(2026, 10, 5), LocalDate.of(2026, 10, 18)),
                    new Holiday("Weihnachtsferien", LocalDate.of(2026, 12, 21), LocalDate.of(2027, 1, 2)),
                    new Holiday("Sportferien", LocalDate.of(2027, 2, 8), LocalDate.of(2027, 2, 20))));
        }

        @Test
        @DisplayName("news are rendered newest first")
        void rendersNewsNewestFirst() throws Exception {
            mockMvc.perform(get("/api/dashboard"))
                    .andExpect(status().isOk())
                    .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                    .andExpect(jsonPath("$.news", hasSize(3)))
                    .andExpect(jsonPath("$.news[0].id").value(3))
                    .andExpect(jsonPath("$.news[0].title").value("Tag der offenen Tür 2026"))
                    .andExpect(jsonPath("$.news[0].text").isNotEmpty())
                    .andExpect(jsonPath("$.news[0].publishedAt").value("2026-10-28"))
                    .andExpect(jsonPath("$.news[2].id").value(1));
        }

        @Test
        @DisplayName("upcoming holidays and own events are merged in date order, past holidays are dropped")
        void mergesHolidaysAndEvents() throws Exception {
            mockMvc.perform(get("/api/dashboard"))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.events", hasSize(6)))
                    .andExpect(jsonPath("$.events[*].title", not(hasItem("Herbstferien"))))
                    .andExpect(jsonPath("$.events[0].title").value("Elternabend Abteilung IT"))
                    .andExpect(jsonPath("$.events[0].kind").value("EVENT"))
                    .andExpect(jsonPath("$.events[0].startDate").value("2026-11-19"))
                    .andExpect(jsonPath("$.events[0].endDate").value("2026-11-19"))
                    .andExpect(jsonPath("$.events[1].title").value("Tag der offenen Tür"))
                    .andExpect(jsonPath("$.events[2].title").value("Weihnachtsferien"))
                    .andExpect(jsonPath("$.events[2].kind").value("HOLIDAY"))
                    .andExpect(jsonPath("$.events[2].startDate").value("2026-12-21"))
                    .andExpect(jsonPath("$.events[2].endDate").value("2027-01-02"))
                    .andExpect(jsonPath("$.events[3].title").value("Prüfungswoche"))
                    .andExpect(jsonPath("$.events[4].title").value("Schulschluss 1. Semester"))
                    .andExpect(jsonPath("$.events[5].title").value("Sportferien"))
                    .andExpect(jsonPath("$.events[5].kind").value("HOLIDAY"));
        }

        @Test
        @DisplayName("prevention notice, Meine Links and Quick Links are rendered")
        void rendersPreventionAndLinks() throws Exception {
            mockMvc.perform(get("/api/dashboard"))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.prevention.title").value("Prävention"))
                    .andExpect(jsonPath("$.prevention.text")
                            .value("147.ch unterstützt junge Menschen bei Sorgen & Problemen."))
                    .andExpect(jsonPath("$.prevention.linkText").value("www.147.ch"))
                    .andExpect(jsonPath("$.prevention.url").value("https://www.147.ch"))
                    .andExpect(jsonPath("$.myLinks", hasSize(3)))
                    .andExpect(jsonPath("$.myLinks[0].label").value("Meine Kurse"))
                    .andExpect(jsonPath("$.myLinks[0].url").value("/klassen-kurse"))
                    .andExpect(jsonPath("$.myLinks[0].external").value(false))
                    .andExpect(jsonPath("$.quickLinks", hasSize(8)))
                    .andExpect(jsonPath("$.quickLinks[0].label").value("Service Desk Pro"))
                    .andExpect(jsonPath("$.quickLinks[*].external", everyItem(is(true))));
        }

        @Test
        @DisplayName("when the timetable upstream is down the dashboard still renders with its own events")
        void degradesWhenTimetableIsUnavailable() throws Exception {
            when(timetableClient.fetchHolidays())
                    .thenThrow(new UpstreamUnavailableException("timetable", "connection refused"));

            mockMvc.perform(get("/api/dashboard"))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.events", hasSize(4)))
                    .andExpect(jsonPath("$.events[*].kind", everyItem(is("EVENT"))));
        }
    }

    @Nested
    @SpringBootTest(properties = "betterintranet.modules.timetable.enabled=false")
    @AutoConfigureMockMvc
    @Import(TimetableStub.class)
    class TimetableDisabled {

        @Autowired
        private MockMvc mockMvc;

        @MockitoBean
        private TimetableClient timetableClient;

        @Test
        @DisplayName("a switched-off timetable module leaves the dashboard with its own events only")
        void rendersOwnEventsOnly() throws Exception {
            mockMvc.perform(get("/api/dashboard"))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.events", hasSize(4)))
                    .andExpect(jsonPath("$.events[*].kind", everyItem(is("EVENT"))))
                    .andExpect(jsonPath("$.news", hasSize(3)));
        }
    }

    @Nested
    @SpringBootTest(properties = "betterintranet.modules.dashboard.enabled=false")
    @AutoConfigureMockMvc
    class ModuleDisabled {

        @Autowired
        private MockMvc mockMvc;

        @Test
        @DisplayName("a switched-off dashboard module answers 503")
        void disabledModuleIs503() throws Exception {
            mockMvc.perform(get("/api/dashboard"))
                    .andExpect(status().isServiceUnavailable())
                    .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_PROBLEM_JSON))
                    .andExpect(jsonPath("$.type").value("urn:betterintranet:module-disabled"))
                    .andExpect(jsonPath("$.module").value("dashboard"));
        }
    }
}
