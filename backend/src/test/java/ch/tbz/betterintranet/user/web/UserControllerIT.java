package ch.tbz.betterintranet.user.web;

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

/** {@code GET /api/me} through controller, gateway, handler and the JSON-backed repository. */
class UserControllerIT {

    @Nested
    @SpringBootTest
    @AutoConfigureMockMvc
    class ModuleEnabled {

        @Autowired
        private MockMvc mockMvc;

        @Test
        @DisplayName("without X-User the default user nicola.wehrli is returned")
        void returnsDefaultUser() throws Exception {
            mockMvc.perform(get("/api/me"))
                    .andExpect(status().isOk())
                    .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                    .andExpect(jsonPath("$.username").value("nicola.wehrli"))
                    .andExpect(jsonPath("$.displayName").value("Nicola Wehrli"))
                    .andExpect(jsonPath("$.firstName").value("Nicola"))
                    .andExpect(jsonPath("$.lastName").value("Wehrli"))
                    .andExpect(jsonPath("$.email").value("nicola.wehrli@tbz.ch"))
                    .andExpect(jsonPath("$.className").value("AP 24b"))
                    .andExpect(jsonPath("$.classId").value(7471))
                    .andExpect(jsonPath("$.role").value("STUDENT"));
        }

        @Test
        @DisplayName("the X-User header selects the user")
        void returnsUserFromHeader() throws Exception {
            mockMvc.perform(get("/api/me").header("X-User", "max.muster"))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.username").value("max.muster"))
                    .andExpect(jsonPath("$.className").value("PE 26a"))
                    .andExpect(jsonPath("$.classId").value(7372));
        }

        @Test
        @DisplayName("an unknown user answers 404 with a problem detail")
        void unknownUserIs404() throws Exception {
            mockMvc.perform(get("/api/me").header("X-User", "unknown.person"))
                    .andExpect(status().isNotFound())
                    .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_PROBLEM_JSON))
                    .andExpect(jsonPath("$.type").value("urn:betterintranet:not-found"))
                    .andExpect(jsonPath("$.detail").value("User unknown.person not found"));
        }
    }

    @Nested
    @SpringBootTest(properties = "betterintranet.modules.user.enabled=false")
    @AutoConfigureMockMvc
    class ModuleDisabled {

        @Autowired
        private MockMvc mockMvc;

        @Test
        @DisplayName("a switched-off user module answers 503")
        void disabledModuleIs503() throws Exception {
            mockMvc.perform(get("/api/me"))
                    .andExpect(status().isServiceUnavailable())
                    .andExpect(jsonPath("$.type").value("urn:betterintranet:module-disabled"))
                    .andExpect(jsonPath("$.module").value("user"));
        }
    }
}
