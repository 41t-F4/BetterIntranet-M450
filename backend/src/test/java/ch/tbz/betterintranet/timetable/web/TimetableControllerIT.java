package ch.tbz.betterintranet.timetable.web;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import ch.tbz.betterintranet.core.error.UpstreamUnavailableException;
import ch.tbz.betterintranet.timetable.api.Holiday;
import ch.tbz.betterintranet.timetable.api.Lesson;
import ch.tbz.betterintranet.timetable.api.LessonStatus;
import ch.tbz.betterintranet.timetable.api.SchoolClass;
import ch.tbz.betterintranet.timetable.application.TimetableClient;
import java.time.DayOfWeek;
import java.time.Clock;
import java.time.Instant;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Import;
import org.springframework.context.annotation.Primary;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.ZoneId;
import java.util.List;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

/** Controller -> gateway -> handler with the upstream client replaced by a Mockito mock. */
class TimetableControllerIT {

    private static final long AP24B = 7471;
    private static final LocalDate MONDAY = LocalDate.of(2026, 11, 2);
    private static final LocalDate TUESDAY = LocalDate.of(2026, 11, 3);
    private static final LocalDate FRIDAY = LocalDate.of(2026, 11, 6);

    /** Replaces the application clock with Wednesday 2026-11-04, 10:00 in Zurich. */
    @TestConfiguration
    static class FixedClock {

        @Bean
        @Primary
        Clock fixedClock() {
            return Clock.fixed(Instant.parse("2026-11-04T09:00:00Z"), ZoneId.of("Europe/Zurich"));
        }
    }

    @Nested
    @SpringBootTest
    @AutoConfigureMockMvc
    @Import(FixedClock.class)
    class ModuleEnabled {

        @Autowired
        private MockMvc mockMvc;

        @MockitoBean
        private TimetableClient client;

        @Test
        @DisplayName("GET /api/timetable/classes lists classes sorted by short name")
        void listsClasses() throws Exception {
            given(client.fetchClasses()).willReturn(List.of(
                    new SchoolClass(7372, "PE 26a", "Plattformentwicklung", "IT"),
                    new SchoolClass(AP24B, "AP 24b", "Applikationsentwicklung", "IT")));

            mockMvc.perform(get("/api/timetable/classes"))
                    .andExpect(status().isOk())
                    .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                    .andExpect(jsonPath("$.length()").value(2))
                    .andExpect(jsonPath("$[0].id").value(7471))
                    .andExpect(jsonPath("$[0].shortName").value("AP 24b"))
                    .andExpect(jsonPath("$[0].longName").value("Applikationsentwicklung"))
                    .andExpect(jsonPath("$[0].department").value("IT"))
                    .andExpect(jsonPath("$[1].shortName").value("PE 26a"));
        }

        @Test
        @DisplayName("GET /api/timetable?classId&from&to returns the lessons of that range as ISO dates and times")
        void listsLessonsOfRange() throws Exception {
            given(client.fetchLessons(AP24B, MONDAY, FRIDAY)).willReturn(List.of(
                    new Lesson(TUESDAY, LocalTime.of(8, 10), LocalTime.of(9, 45), "ENG", "Englisch", List.of("MID"), List.of("AU-512"),
                            LessonStatus.REGULAR, "oB"),
                    new Lesson(TUESDAY, LocalTime.of(10, 5), LocalTime.of(12, 30), "GES", "Geschichte", List.of("RIA"), List.of(),
                            LessonStatus.CANCELLED, null)));

            mockMvc.perform(get("/api/timetable")
                            .queryParam("classId", "7471")
                            .queryParam("from", "2026-11-02")
                            .queryParam("to", "2026-11-06"))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.length()").value(2))
                    .andExpect(jsonPath("$[0].date").value("2026-11-03"))
                    .andExpect(jsonPath("$[0].start").value("08:10:00"))
                    .andExpect(jsonPath("$[0].end").value("09:45:00"))
                    .andExpect(jsonPath("$[0].subject").value("ENG"))
                    .andExpect(jsonPath("$[0].subjectName").value("Englisch"))
                    .andExpect(jsonPath("$[0].teachers[0]").value("MID"))
                    .andExpect(jsonPath("$[0].rooms[0]").value("AU-512"))
                    .andExpect(jsonPath("$[0].status").value("REGULAR"))
                    .andExpect(jsonPath("$[0].info").value("oB"))
                    .andExpect(jsonPath("$[1].status").value("CANCELLED"))
                    .andExpect(jsonPath("$[1].rooms").isEmpty());
        }

        @Test
        @DisplayName("without from/to the current ISO week Monday..Friday is requested")
        void defaultsToCurrentWeek() throws Exception {
            given(client.fetchLessons(anyLong(), any(), any())).willReturn(List.of());

            mockMvc.perform(get("/api/timetable").queryParam("classId", "7471"))
                    .andExpect(status().isOk())
                    .andExpect(content().json("[]"));

            ArgumentCaptor<LocalDate> from = ArgumentCaptor.forClass(LocalDate.class);
            ArgumentCaptor<LocalDate> to = ArgumentCaptor.forClass(LocalDate.class);
            verify(client).fetchLessons(eq(AP24B), from.capture(), to.capture());
            // The clock is fixed to Wednesday 2026-11-04 (see FixedClock), so the week is known exactly.
            assertThat(from.getValue()).isEqualTo(MONDAY);
            assertThat(to.getValue()).isEqualTo(FRIDAY);
        }

        @Test
        @DisplayName("a missing classId is a 400")
        void missingClassIdIsBadRequest() throws Exception {
            mockMvc.perform(get("/api/timetable").queryParam("from", "2026-11-02"))
                    .andExpect(status().isBadRequest());
            verifyNoInteractions(client);
        }

        @Test
        @DisplayName("a date that is not ISO formatted is a 400")
        void malformedDateIsBadRequest() throws Exception {
            mockMvc.perform(get("/api/timetable").queryParam("classId", "7471").queryParam("from", "02.11.2026"))
                    .andExpect(status().isBadRequest());
            verifyNoInteractions(client);
        }

        @Test
        @DisplayName("'to' before 'from' is a 400 problem detail")
        void invertedRangeIsBadRequest() throws Exception {
            mockMvc.perform(get("/api/timetable")
                            .queryParam("classId", "7471")
                            .queryParam("from", "2026-11-06")
                            .queryParam("to", "2026-11-02"))
                    .andExpect(status().isBadRequest())
                    .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_PROBLEM_JSON))
                    .andExpect(jsonPath("$.status").value(400))
                    .andExpect(jsonPath("$.type").value("urn:betterintranet:invalid-request"))
                    .andExpect(jsonPath("$.detail").value("'to' must not be before 'from'"));
            verifyNoInteractions(client);
        }

        @Test
        @DisplayName("a range longer than 62 days is a 400 problem detail and never reaches the upstream")
        void tooLongRangeIsBadRequest() throws Exception {
            mockMvc.perform(get("/api/timetable")
                            .queryParam("classId", "7471")
                            .queryParam("from", "2026-08-17")
                            .queryParam("to", "2027-07-16"))
                    .andExpect(status().isBadRequest())
                    .andExpect(jsonPath("$.type").value("urn:betterintranet:invalid-request"))
                    .andExpect(jsonPath("$.detail").value("The range must not be longer than 62 days, but was 334"));
            verifyNoInteractions(client);
        }

        @Test
        @DisplayName("an IllegalArgumentException from deeper layers is a 500 without internal details, not a 400")
        void internalIllegalArgumentIsNotBadRequest() throws Exception {
            given(client.fetchLessons(anyLong(), any(), any()))
                    .willThrow(new IllegalArgumentException("internal detail: parser state 17"));

            mockMvc.perform(get("/api/timetable").queryParam("classId", "7471"))
                    .andExpect(status().isInternalServerError())
                    .andExpect(jsonPath("$.type").value("urn:betterintranet:internal"))
                    .andExpect(jsonPath("$.detail").value("The request could not be processed."));
        }

        @Test
        @DisplayName("GET /api/timetable/holidays lists holidays sorted by start date")
        void listsHolidays() throws Exception {
            given(client.fetchHolidays()).willReturn(List.of(
                    new Holiday("Weihnachtsferien", LocalDate.of(2026, 12, 21), LocalDate.of(2027, 1, 3)),
                    new Holiday("Herbstferien", LocalDate.of(2026, 10, 5), LocalDate.of(2026, 10, 18))));

            mockMvc.perform(get("/api/timetable/holidays"))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.length()").value(2))
                    .andExpect(jsonPath("$[0].name").value("Herbstferien"))
                    .andExpect(jsonPath("$[0].startDate").value("2026-10-05"))
                    .andExpect(jsonPath("$[0].endDate").value("2026-10-18"))
                    .andExpect(jsonPath("$[1].name").value("Weihnachtsferien"));
        }

        @Test
        @DisplayName("an unreachable upstream is a 502 problem detail")
        void upstreamFailureIsBadGateway() throws Exception {
            given(client.fetchHolidays()).willThrow(new UpstreamUnavailableException("timetable", "connection refused"));

            mockMvc.perform(get("/api/timetable/holidays"))
                    .andExpect(status().isBadGateway())
                    .andExpect(jsonPath("$.upstream").value("timetable"));
        }
    }

    @Nested
    @SpringBootTest(properties = "betterintranet.modules.timetable.enabled=false")
    @AutoConfigureMockMvc
    class ModuleDisabled {

        @Autowired
        private MockMvc mockMvc;

        @MockitoBean
        private TimetableClient client;

        @Test
        @DisplayName("every timetable endpoint answers 503 naming the module")
        void allEndpointsAnswer503() throws Exception {
            for (String path : List.of("/api/timetable/classes", "/api/timetable?classId=7471", "/api/timetable/holidays")) {
                mockMvc.perform(get(path))
                        .andExpect(status().isServiceUnavailable())
                        .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_PROBLEM_JSON))
                        .andExpect(jsonPath("$.title").value("Module disabled"))
                        .andExpect(jsonPath("$.module").value("timetable"));
            }
            verifyNoInteractions(client);
        }
    }
}
