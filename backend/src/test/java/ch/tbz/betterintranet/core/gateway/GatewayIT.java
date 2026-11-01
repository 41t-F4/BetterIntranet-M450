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
