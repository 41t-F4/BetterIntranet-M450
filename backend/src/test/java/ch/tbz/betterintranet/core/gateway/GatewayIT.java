package ch.tbz.betterintranet.core.gateway;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

/** Exercises controller -> gateway -> handler wiring and the HTTP error mapping with a real Spring context. */
class GatewayIT {

    @Nested
    @SpringBootTest
    @AutoConfigureMockMvc
    class ModuleEnabled {

        @Autowired
        private MockMvc mockMvc;

        @Test
        @DisplayName("a request is routed through the gateway to the module handler")
        void routesThroughGateway() throws Exception {
            mockMvc.perform(get("/api/test/ping").header("X-User", "max.muster"))
                    .andExpect(status().isOk())
                    .andExpect(content().string("pong:max.muster"));
        }

        @Test
        @DisplayName("without X-User the configured default user is used")
        void defaultsUser() throws Exception {
            mockMvc.perform(get("/api/test/ping"))
                    .andExpect(status().isOk())
                    .andExpect(content().string("pong:nicola.wehrli"));
        }

        @Test
        @DisplayName("NotFoundException is rendered as a 404 problem detail")
        void notFoundProblem() throws Exception {
            mockMvc.perform(get("/api/test/not-found"))
                    .andExpect(status().isNotFound())
                    .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_PROBLEM_JSON))
                    .andExpect(jsonPath("$.status").value(404))
                    .andExpect(jsonPath("$.detail").value("nothing here"));
        }

        @Test
        @DisplayName("UpstreamUnavailableException is rendered as a 502 problem detail")
        void upstreamProblem() throws Exception {
            mockMvc.perform(get("/api/test/upstream"))
                    .andExpect(status().isBadGateway())
                    .andExpect(jsonPath("$.status").value(502))
                    .andExpect(jsonPath("$.upstream").value("timetable"));
        }
    }

    @Nested
    @SpringBootTest
    @AutoConfigureMockMvc
    class ErrorMapping {

        @Autowired
        private MockMvc mockMvc;

        @Test
        @DisplayName("InvalidRequestException is rendered as a 400 problem detail with its message")
        void invalidRequestProblem() throws Exception {
            mockMvc.perform(get("/api/test/invalid"))
                    .andExpect(status().isBadRequest())
                    .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_PROBLEM_JSON))
                    .andExpect(jsonPath("$.type").value("urn:betterintranet:invalid-request"))
                    .andExpect(jsonPath("$.title").value("Invalid request"))
                    .andExpect(jsonPath("$.detail").value("The range must not be longer than 62 days."));
        }

        @Test
        @DisplayName("an unexpected exception becomes a 500 problem detail without internal details")
        void unexpectedErrorProblem() throws Exception {
            mockMvc.perform(get("/api/test/boom"))
                    .andExpect(status().isInternalServerError())
                    .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_PROBLEM_JSON))
                    .andExpect(jsonPath("$.type").value("urn:betterintranet:internal"))
                    .andExpect(jsonPath("$.detail").value("The request could not be processed."));
        }

        @Test
        @DisplayName("a missing request parameter is a 400 problem detail")
        void missingParameterProblem() throws Exception {
            mockMvc.perform(get("/api/test/number"))
                    .andExpect(status().isBadRequest())
                    .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_PROBLEM_JSON))
                    .andExpect(jsonPath("$.status").value(400));
        }

        @Test
        @DisplayName("a request parameter of the wrong type is a 400 problem detail")
        void typeMismatchProblem() throws Exception {
            mockMvc.perform(get("/api/test/number").param("value", "abc"))
                    .andExpect(status().isBadRequest())
                    .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_PROBLEM_JSON))
                    .andExpect(jsonPath("$.status").value(400));
        }

        @Test
        @DisplayName("an unknown path stays a 404 problem detail")
        void unknownPathProblem() throws Exception {
            mockMvc.perform(get("/api/does-not-exist"))
                    .andExpect(status().isNotFound())
                    .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_PROBLEM_JSON));
        }
    }

    @Nested
    @SpringBootTest(properties = "betterintranet.modules.testmodule.enabled=false")
    @AutoConfigureMockMvc
    class ModuleDisabled {

        @Autowired
        private MockMvc mockMvc;

        @Test
        @DisplayName("a disabled module answers 503 with a problem detail naming the module")
        void disabledModuleAnswers503() throws Exception {
            mockMvc.perform(get("/api/test/ping"))
                    .andExpect(status().isServiceUnavailable())
                    .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_PROBLEM_JSON))
                    .andExpect(jsonPath("$.status").value(503))
                    .andExpect(jsonPath("$.title").value("Module disabled"))
                    .andExpect(jsonPath("$.module").value("testmodule"));
        }
    }
}
