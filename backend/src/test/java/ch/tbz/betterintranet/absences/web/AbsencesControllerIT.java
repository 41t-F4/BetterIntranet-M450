package ch.tbz.betterintranet.absences.web;

import static ch.tbz.betterintranet.absences.api.AbsenceStatus.EXCUSED;
import static ch.tbz.betterintranet.absences.api.AbsenceStatus.OPEN;
import static ch.tbz.betterintranet.absences.api.AbsenceStatus.UNEXCUSED;
import static org.hamcrest.Matchers.allOf;
import static org.hamcrest.Matchers.containsString;
import static org.hamcrest.Matchers.hasSize;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import ch.tbz.betterintranet.absences.api.Absence;
import ch.tbz.betterintranet.absences.api.AbsenceStatus;
import ch.tbz.betterintranet.absences.application.AbsencesClient;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

/** Controller -> gateway -> handler with a real Spring context; the school system is replaced by a Mockito mock. */
class AbsencesControllerIT {

    private static final List<Absence> ABSENCES = List.of(
            absence(1, LocalDate.of(2026, 9, 14), LocalTime.of(8, 10), LocalTime.of(8, 55), UNEXCUSED, null),
            absence(2, LocalDate.of(2026, 10, 12), LocalTime.of(8, 10), LocalTime.of(9, 45), OPEN, null),
            absence(3, LocalDate.of(2026, 9, 28), LocalTime.of(13, 15), LocalTime.of(14, 50), EXCUSED, "Arztbesuch"));

    @Nested
    @SpringBootTest
    @AutoConfigureMockMvc
    class ModuleEnabled {

        @Autowired
        private MockMvc mockMvc;

        @MockitoBean
        private AbsencesClient client;

        @Test
        @DisplayName("GET /api/absences returns the summary and the absences newest first")
        void returnsOverview() throws Exception {
            when(client.fetchAbsences("nicola.wehrli")).thenReturn(ABSENCES);

            mockMvc.perform(get("/api/absences"))
                    .andExpect(status().isOk())
                    .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                    .andExpect(jsonPath("$.summary.total").value(3))
                    .andExpect(jsonPath("$.summary.open").value(1))
                    .andExpect(jsonPath("$.summary.excused").value(1))
                    .andExpect(jsonPath("$.summary.unexcused").value(1))
                    .andExpect(jsonPath("$.summary.totalLessons").value(5))
                    .andExpect(jsonPath("$.absences", hasSize(3)))
                    .andExpect(jsonPath("$.absences[0].id").value(2))
                    .andExpect(jsonPath("$.absences[0].date").value("2026-10-12"))
                    .andExpect(jsonPath("$.absences[0].lessonStart").value("08:10:00"))
                    .andExpect(jsonPath("$.absences[0].lessonEnd").value("09:45:00"))
                    .andExpect(jsonPath("$.absences[0].subject").value("Englisch"))
                    .andExpect(jsonPath("$.absences[0].teacher").value("MID"))
                    .andExpect(jsonPath("$.absences[0].status").value("OPEN"))
                    .andExpect(jsonPath("$.absences[0].reason").doesNotExist())
                    .andExpect(jsonPath("$.absences[0].reportedAt").value("2026-10-12T10:00:00"))
                    .andExpect(jsonPath("$.absences[1].id").value(3))
                    .andExpect(jsonPath("$.absences[1].reason").value("Arztbesuch"))
                    .andExpect(jsonPath("$.absences[2].id").value(1));
        }

        @Test
        @DisplayName("?status=OPEN only lists open absences while the summary stays complete")
        void filtersByStatus() throws Exception {
            when(client.fetchAbsences("nicola.wehrli")).thenReturn(ABSENCES);

            mockMvc.perform(get("/api/absences").param("status", "OPEN"))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.absences", hasSize(1)))
                    .andExpect(jsonPath("$.absences[0].status").value("OPEN"))
                    .andExpect(jsonPath("$.summary.total").value(3));
        }

        @Test
        @DisplayName("an unknown status value is a 400 problem detail that names the valid values")
        void rejectsUnknownStatus() throws Exception {
            mockMvc.perform(get("/api/absences").param("status", "LATE"))
                    .andExpect(status().isBadRequest())
                    .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_PROBLEM_JSON))
                    .andExpect(jsonPath("$.status").value(400))
                    .andExpect(jsonPath("$.detail").value(allOf(containsString("LATE"), containsString("OPEN"),
                            containsString("EXCUSED"), containsString("UNEXCUSED"))));

            verifyNoInteractions(client);
        }

        @Test
        @DisplayName("the X-User header decides whose absences are loaded")
        void forwardsUserHeader() throws Exception {
            when(client.fetchAbsences(anyString())).thenReturn(List.of());

            mockMvc.perform(get("/api/absences").header("X-User", "max.muster"))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.absences", hasSize(0)));

            verify(client).fetchAbsences("max.muster");
        }

        @Test
        @DisplayName("without X-User the configured default user is used")
        void defaultsUser() throws Exception {
            when(client.fetchAbsences(anyString())).thenReturn(List.of());

            mockMvc.perform(get("/api/absences"))
                    .andExpect(status().isOk());

            verify(client).fetchAbsences("nicola.wehrli");
        }
    }

    @Nested
    @SpringBootTest(properties = "betterintranet.modules.absences.enabled=false")
    @AutoConfigureMockMvc
    class ModuleDisabled {

        @Autowired
        private MockMvc mockMvc;

        @MockitoBean
        private AbsencesClient client;

        @Test
        @DisplayName("a disabled absences module answers 503 without touching the school system")
        void answers503() throws Exception {
            mockMvc.perform(get("/api/absences"))
                    .andExpect(status().isServiceUnavailable())
                    .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_PROBLEM_JSON))
                    .andExpect(jsonPath("$.status").value(503))
                    .andExpect(jsonPath("$.module").value("absences"));

            verifyNoInteractions(client);
        }
    }

    private static Absence absence(long id, LocalDate date, LocalTime start, LocalTime end, AbsenceStatus status,
                                   String reason) {
        return new Absence(id, date, start, end, "Englisch", "MID", status, reason, date.atTime(10, 0));
    }
}
