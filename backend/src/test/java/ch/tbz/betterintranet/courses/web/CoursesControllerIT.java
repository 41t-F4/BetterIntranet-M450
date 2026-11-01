package ch.tbz.betterintranet.courses.web;

import static org.hamcrest.Matchers.hasSize;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import ch.tbz.betterintranet.core.error.NotFoundException;
import ch.tbz.betterintranet.courses.api.Course;
import ch.tbz.betterintranet.courses.application.CoursesClient;
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
class CoursesControllerIT {

    private static final Course M450 = new Course("450-HS26", "450", "Applikationen testen", "VAJ", "HS 2026", 4,
            "AU-060");
    private static final Course ENG_HS26 = new Course("ENG-HS26", "ENG", "Englisch", "MID", "HS 2026", 2, "AU-512");
    private static final Course M320_FS26 = new Course("320-FS26", "320", "Objektorientiert programmieren", "HEA",
            "FS 2026", 4, "AU-060");

    @Nested
    @SpringBootTest
    @AutoConfigureMockMvc
    class ModuleEnabled {

        @Autowired
        private MockMvc mockMvc;

        @MockitoBean
        private CoursesClient coursesClient;

        @Test
        @DisplayName("GET /api/courses returns the courses of the X-User as JSON, newest semester first")
        void returnsCoursesOfCurrentUser() throws Exception {
            when(coursesClient.coursesOf("nicola.wehrli")).thenReturn(List.of(M320_FS26, ENG_HS26, M450));

            mockMvc.perform(get("/api/courses").header("X-User", "nicola.wehrli"))
                    .andExpect(status().isOk())
                    .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                    .andExpect(jsonPath("$").value(hasSize(3)))
                    .andExpect(jsonPath("$[0].id").value("450-HS26"))
                    .andExpect(jsonPath("$[0].code").value("450"))
                    .andExpect(jsonPath("$[0].name").value("Applikationen testen"))
                    .andExpect(jsonPath("$[0].teacher").value("VAJ"))
                    .andExpect(jsonPath("$[0].semester").value("HS 2026"))
                    .andExpect(jsonPath("$[0].lessonsPerWeek").value(4))
                    .andExpect(jsonPath("$[0].room").value("AU-060"))
                    .andExpect(jsonPath("$[0].*").value(hasSize(7)))
                    .andExpect(jsonPath("$[1].id").value("ENG-HS26"))
                    .andExpect(jsonPath("$[2].id").value("320-FS26"));
        }

        @Test
        @DisplayName("GET /api/courses?semester=HS 2026 only returns courses of that semester")
        void filtersBySemester() throws Exception {
            when(coursesClient.coursesOf("nicola.wehrli")).thenReturn(List.of(M320_FS26, ENG_HS26, M450));

            mockMvc.perform(get("/api/courses").queryParam("semester", "HS 2026").header("X-User", "nicola.wehrli"))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$").value(hasSize(2)))
                    .andExpect(jsonPath("$[0].id").value("450-HS26"))
                    .andExpect(jsonPath("$[1].id").value("ENG-HS26"));
        }

        @Test
        @DisplayName("the X-User header decides whose courses are loaded")
        void usesXUserHeader() throws Exception {
            when(coursesClient.coursesOf("max.muster")).thenReturn(List.of());

            mockMvc.perform(get("/api/courses").header("X-User", "max.muster"))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$").value(hasSize(0)));

            verify(coursesClient).coursesOf("max.muster");
        }

        @Test
        @DisplayName("without X-User the configured default user is used")
        void defaultsToConfiguredUser() throws Exception {
            when(coursesClient.coursesOf("nicola.wehrli")).thenReturn(List.of(M450));

            mockMvc.perform(get("/api/courses"))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$[0].id").value("450-HS26"));
        }

        @Test
        @DisplayName("an unknown user is answered with a 404 problem detail")
        void unknownUserIs404() throws Exception {
            when(coursesClient.coursesOf("unknown")).thenThrow(new NotFoundException("Unknown user 'unknown'"));

            mockMvc.perform(get("/api/courses").header("X-User", "unknown"))
                    .andExpect(status().isNotFound())
                    .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_PROBLEM_JSON))
                    .andExpect(jsonPath("$.detail").value("Unknown user 'unknown'"));
        }
    }

    @Nested
    @SpringBootTest(properties = "betterintranet.modules.courses.enabled=false")
    @AutoConfigureMockMvc
    class ModuleDisabled {

        @Autowired
        private MockMvc mockMvc;

        @MockitoBean
        private CoursesClient coursesClient;

        @Test
        @DisplayName("GET /api/courses answers 503 when the courses module is switched off")
        void answers503WhenDisabled() throws Exception {
            mockMvc.perform(get("/api/courses"))
                    .andExpect(status().isServiceUnavailable())
                    .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_PROBLEM_JSON))
                    .andExpect(jsonPath("$.title").value("Module disabled"))
                    .andExpect(jsonPath("$.module").value("courses"));
        }
    }

    @Nested
    @SpringBootTest(properties = "betterintranet.modules.classes.enabled=false")
    @AutoConfigureMockMvc
    class ClassesModuleDisabled {

        @Autowired
        private MockMvc mockMvc;

        @MockitoBean
        private CoursesClient coursesClient;

        @Test
        @DisplayName("switching off the classes module does not affect GET /api/courses")
        void coursesStayAvailable() throws Exception {
            when(coursesClient.coursesOf("nicola.wehrli")).thenReturn(List.of(M450));

            mockMvc.perform(get("/api/courses"))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$[0].id").value("450-HS26"));

            mockMvc.perform(get("/api/classes"))
                    .andExpect(status().isServiceUnavailable());
        }
    }
}
