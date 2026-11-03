package ch.tbz.betterintranet.classes.web;

import static org.hamcrest.Matchers.hasSize;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import ch.tbz.betterintranet.classes.api.SchoolClassMembership;
import ch.tbz.betterintranet.classes.application.ClassesClient;
import ch.tbz.betterintranet.core.error.NotFoundException;
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

/** Controller -> gateway -> handler with the school client mocked out. */
class ClassesControllerIT {

    private static final SchoolClassMembership AP24B_2026 = new SchoolClassMembership(7471, "AP 24b",
            "Informatiker/innen Applikationsentwicklung", "IT", "MID", "2026/27", "STUDENT");
    private static final SchoolClassMembership AP24B_2025 = new SchoolClassMembership(6890, "AP 24b",
            "Informatiker/innen Applikationsentwicklung", "IT", "MID", "2025/26", "STUDENT");

    @Nested
    @SpringBootTest
    @AutoConfigureMockMvc
    class ModuleEnabled {

        @Autowired
        private MockMvc mockMvc;

        @MockitoBean
        private ClassesClient classesClient;

        @Test
        @DisplayName("GET /api/classes returns the sorted classes of the X-User as JSON")
        void returnsClassesOfCurrentUser() throws Exception {
            when(classesClient.classesOf("nicola.wehrli")).thenReturn(List.of(AP24B_2025, AP24B_2026));

            mockMvc.perform(get("/api/classes").header("X-User", "nicola.wehrli"))
                    .andExpect(status().isOk())
                    .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                    .andExpect(jsonPath("$").value(hasSize(2)))
                    .andExpect(jsonPath("$[0].id").value(7471))
                    .andExpect(jsonPath("$[0].shortName").value("AP 24b"))
                    .andExpect(jsonPath("$[0].longName").value("Informatiker/innen Applikationsentwicklung"))
                    .andExpect(jsonPath("$[0].department").value("IT"))
                    .andExpect(jsonPath("$[0].classTeacher").value("MID"))
                    .andExpect(jsonPath("$[0].schoolYear").value("2026/27"))
                    .andExpect(jsonPath("$[0].role").value("STUDENT"))
                    .andExpect(jsonPath("$[1].schoolYear").value("2025/26"));
        }

        @Test
        @DisplayName("the X-User header decides whose classes are loaded")
        void usesXUserHeader() throws Exception {
            when(classesClient.classesOf("max.muster")).thenReturn(List.of());

            mockMvc.perform(get("/api/classes").header("X-User", "max.muster"))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$").value(hasSize(0)));

            verify(classesClient).classesOf("max.muster");
        }

        @Test
        @DisplayName("without X-User the configured default user is used")
        void defaultsToConfiguredUser() throws Exception {
            when(classesClient.classesOf("nicola.wehrli")).thenReturn(List.of(AP24B_2026));

            mockMvc.perform(get("/api/classes"))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$[0].id").value(7471));
        }

        @Test
        @DisplayName("an unknown user is answered with a 404 problem detail")
        void unknownUserIs404() throws Exception {
            when(classesClient.classesOf("unknown")).thenThrow(new NotFoundException("Unknown user 'unknown'"));

            mockMvc.perform(get("/api/classes").header("X-User", "unknown"))
                    .andExpect(status().isNotFound())
                    .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_PROBLEM_JSON))
                    .andExpect(jsonPath("$.detail").value("Unknown user 'unknown'"));
        }
    }

    @Nested
    @SpringBootTest(properties = "betterintranet.modules.classes.enabled=false")
    @AutoConfigureMockMvc
    class ModuleDisabled {

        @Autowired
        private MockMvc mockMvc;

        @MockitoBean
        private ClassesClient classesClient;

        @Test
        @DisplayName("GET /api/classes answers 503 when the classes module is switched off")
        void answers503WhenDisabled() throws Exception {
            mockMvc.perform(get("/api/classes"))
                    .andExpect(status().isServiceUnavailable())
                    .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_PROBLEM_JSON))
                    .andExpect(jsonPath("$.title").value("Module disabled"))
                    .andExpect(jsonPath("$.module").value("classes"));
        }
    }
}
