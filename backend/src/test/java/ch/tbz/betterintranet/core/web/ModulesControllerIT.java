package ch.tbz.betterintranet.core.web;

import static org.hamcrest.Matchers.hasItem;
import static org.hamcrest.Matchers.hasSize;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.test.web.servlet.MockMvc;

@SpringBootTest(properties = "betterintranet.modules.grades.enabled=false")
@AutoConfigureMockMvc
class ModulesControllerIT {

    @Autowired
    private MockMvc mockMvc;

    @Test
    @DisplayName("GET /api/modules lists every configured module with its switch state")
    void listsModules() throws Exception {
        mockMvc.perform(get("/api/modules"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[?(@.name == 'grades')].enabled").value(hasItem(false)))
                .andExpect(jsonPath("$[?(@.name == 'timetable')].enabled").value(hasItem(true)))
                .andExpect(jsonPath("$[?(@.name == 'testmodule')]").value(hasSize(1)))
                .andExpect(jsonPath("$[?(@.name == 'core')]").value(hasSize(0)));
    }
}
