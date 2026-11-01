package ch.tbz.betterintranet.grades.web;

import static org.hamcrest.Matchers.aMapWithSize;
import static org.hamcrest.Matchers.allOf;
import static org.hamcrest.Matchers.hasKey;
import static org.hamcrest.Matchers.nullValue;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import ch.tbz.betterintranet.core.error.NotFoundException;
import ch.tbz.betterintranet.core.error.UpstreamUnavailableException;
import ch.tbz.betterintranet.grades.application.GradesClient;
import ch.tbz.betterintranet.grades.application.RawGrade;
import java.time.LocalDate;
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

/** Controller -> gateway -> handler with the school system mocked at the {@link GradesClient} port. */
class GradesControllerIT {

    @Nested
    @SpringBootTest
    @AutoConfigureMockMvc
    class ModuleEnabled {

        @Autowired
        private MockMvc mockMvc;

        @MockitoBean
        private GradesClient client;

        @Test
        @DisplayName("GET /api/grades returns a flat subject list, newest semester first, as JSON")
        void shouldReturnOverviewJson() throws Exception {
            when(client.fetchGrades("nicola.wehrli")).thenReturn(List.of(
                    new RawGrade(1, "Englisch", "FS 2026", "Listening", LocalDate.of(2026, 5, 21), 5.0, 1.0),
                    new RawGrade(6, "450 Applikationen testen", "HS 2026", "LB1 Testkonzept",
                            LocalDate.of(2026, 9, 29), 5.5, 1.0),
                    new RawGrade(7, "450 Applikationen testen", "HS 2026", "LB2 Projektarbeit",
                            LocalDate.of(2026, 10, 27), 5.0, 2.0)));

            mockMvc.perform(get("/api/grades"))
                    .andExpect(status().isOk())
                    .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                    .andExpect(jsonPath("$.overallAverage").value(5.09))
                    .andExpect(jsonPath("$", allOf(aMapWithSize(2), hasKey("overallAverage"), hasKey("subjects"))))
                    .andExpect(jsonPath("$.subjects.length()").value(2))
                    .andExpect(jsonPath("$.subjects[0].subject").value("450 Applikationen testen"))
                    .andExpect(jsonPath("$.subjects[0].semester").value("HS 2026"))
                    .andExpect(jsonPath("$.subjects[0].average").value(5.17))
                    .andExpect(jsonPath("$.subjects[0].reportGrade").value(5.0))
                    .andExpect(jsonPath("$.subjects[0]", aMapWithSize(5)))
                    .andExpect(jsonPath("$.subjects[0].grades[0].id").value(7))
                    .andExpect(jsonPath("$.subjects[0].grades[0].title").value("LB2 Projektarbeit"))
                    .andExpect(jsonPath("$.subjects[0].grades[0].date").value("2026-10-27"))
                    .andExpect(jsonPath("$.subjects[0].grades[0].grade").value(5.0))
                    .andExpect(jsonPath("$.subjects[0].grades[0].weight").value(2.0))
                    .andExpect(jsonPath("$.subjects[0].grades[1].id").value(6))
                    .andExpect(jsonPath("$.subjects[1].subject").value("Englisch"))
                    .andExpect(jsonPath("$.subjects[1].semester").value("FS 2026"))
                    .andExpect(jsonPath("$.subjects[1].average").value(5.0));
        }

        @Test
        @DisplayName("a user without grades gets null averages and an empty list")
        void shouldRenderNullAveragesForEmptyOverview() throws Exception {
            when(client.fetchGrades("nicola.wehrli")).thenReturn(List.of());

            mockMvc.perform(get("/api/grades"))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.overallAverage").value(nullValue()))
                    .andExpect(jsonPath("$.subjects").isEmpty());
        }

        @Test
        @DisplayName("the X-User header selects whose grades are fetched")
        void shouldForwardUserFromHeader() throws Exception {
            when(client.fetchGrades(anyString())).thenReturn(List.of());

            mockMvc.perform(get("/api/grades").header("X-User", "max.muster"))
                    .andExpect(status().isOk());

            verify(client).fetchGrades("max.muster");
        }

        @Test
        @DisplayName("without X-User the configured default user is used")
        void shouldUseDefaultUserWithoutHeader() throws Exception {
            when(client.fetchGrades(anyString())).thenReturn(List.of());

            mockMvc.perform(get("/api/grades"))
                    .andExpect(status().isOk());

            verify(client).fetchGrades("nicola.wehrli");
        }

        @Test
        @DisplayName("an unknown user is answered with a 404 problem detail")
        void shouldReturn404ForUnknownUser() throws Exception {
            when(client.fetchGrades("ghost")).thenThrow(new NotFoundException("User ghost not found"));

            mockMvc.perform(get("/api/grades").header("X-User", "ghost"))
                    .andExpect(status().isNotFound())
                    .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_PROBLEM_JSON))
                    .andExpect(jsonPath("$.status").value(404))
                    .andExpect(jsonPath("$.detail").value("User ghost not found"));
        }

        @Test
        @DisplayName("an unreachable school system is answered with a 502 problem detail")
        void shouldReturn502WhenSchoolSystemIsDown() throws Exception {
            when(client.fetchGrades("nicola.wehrli"))
                    .thenThrow(new UpstreamUnavailableException("school", "connection refused"));

            mockMvc.perform(get("/api/grades"))
                    .andExpect(status().isBadGateway())
                    .andExpect(jsonPath("$.status").value(502))
                    .andExpect(jsonPath("$.upstream").value("school"));
        }
    }

    @Nested
    @SpringBootTest(properties = "betterintranet.modules.grades.enabled=false")
    @AutoConfigureMockMvc
    class ModuleDisabled {

        @Autowired
        private MockMvc mockMvc;

        @Test
        @DisplayName("GET /api/grades answers 503 when the grades module is switched off")
        void shouldReturn503WhenGradesModuleIsDisabled() throws Exception {
            mockMvc.perform(get("/api/grades"))
                    .andExpect(status().isServiceUnavailable())
                    .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_PROBLEM_JSON))
                    .andExpect(jsonPath("$.status").value(503))
                    .andExpect(jsonPath("$.module").value("grades"));
        }
    }
}
