package ch.tbz.schoolmock;

import static org.hamcrest.Matchers.nullValue;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

/**
 * End-to-end test through the full Spring context against the real fixture files. Verifies the
 * contract the main backend relies on.
 */
@SpringBootTest
@AutoConfigureMockMvc
class SchoolMockApiIT {

	@Autowired
	private MockMvc mockMvc;

	@Test
	void shouldServeClassesOfNicolaWehrli() throws Exception {
		mockMvc.perform(get("/mock/users/nicola.wehrli/classes"))
				.andExpect(status().isOk())
				.andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
				.andExpect(jsonPath("$.length()").value(1))
				.andExpect(jsonPath("$[0].id").value(7471))
				.andExpect(jsonPath("$[0].shortName").value("AP 24b"))
				.andExpect(jsonPath("$[0].longName").value("Informatiker/innen Applikationsentwicklung"))
				.andExpect(jsonPath("$[0].department").value("IT"))
				.andExpect(jsonPath("$[0].classTeacher").value("MID"))
				.andExpect(jsonPath("$[0].schoolYear").value("2026/27"))
				.andExpect(jsonPath("$[0].role").value("STUDENT"));
	}

	@Test
	void shouldServeCoursesOfNicolaWehrliFromAllSchoolParts() throws Exception {
		mockMvc.perform(get("/mock/users/nicola.wehrli/courses"))
				.andExpect(status().isOk())
				.andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
				.andExpect(jsonPath("$[0].id").value("M450"))
				.andExpect(jsonPath("$[0].name").value("Applikationen testen"))
				.andExpect(jsonPath("$[0].semester").value("HS 2026"))
				.andExpect(jsonPath("$[0].lessonsPerWeek").value(4))
				.andExpect(jsonPath("$[0].room").value("AU-512"))
				.andExpect(jsonPath("$[0].schoolPart").value("TBZ"))
				.andExpect(jsonPath("$[?(@.schoolPart == 'BMS')]").isNotEmpty())
				.andExpect(jsonPath("$[?(@.schoolPart == 'UEK')]").isNotEmpty())
				.andExpect(jsonPath("$[?(@.name == 'Deutsch')].schoolPart").value("BMS"))
				.andExpect(jsonPath("$[?(@.code == 'ÜK 335')].schoolPart").value("UEK"));
	}

	@Test
	void shouldServeAbsencesOfNicolaWehrliWithAllStatuses() throws Exception {
		mockMvc.perform(get("/mock/users/nicola.wehrli/absences"))
				.andExpect(status().isOk())
				.andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
				.andExpect(jsonPath("$.length()").value(8))
				.andExpect(jsonPath("$[0].id").value(1))
				.andExpect(jsonPath("$[0].date").value("2026-10-12"))
				.andExpect(jsonPath("$[0].lessonStart").value("08:10"))
				.andExpect(jsonPath("$[0].lessonEnd").value("09:45"))
				.andExpect(jsonPath("$[0].subject").value("Englisch"))
				.andExpect(jsonPath("$[0].teacher").value("MID"))
				.andExpect(jsonPath("$[0].status").value("OPEN"))
				.andExpect(jsonPath("$[0].reason").value(nullValue()))
				.andExpect(jsonPath("$[0].reportedAt").value("2026-10-12T10:00:00"))
				.andExpect(jsonPath("$[?(@.status == 'OPEN')]").isNotEmpty())
				.andExpect(jsonPath("$[?(@.status == 'EXCUSED')]").isNotEmpty())
				.andExpect(jsonPath("$[?(@.status == 'UNEXCUSED')]").isNotEmpty())
				.andExpect(jsonPath("$[?(@.id == 3)].reason").value("Arztbesuch"));
	}

	@Test
	void shouldServeGradesOfNicolaWehrliAsFlatList() throws Exception {
		mockMvc.perform(get("/mock/users/nicola.wehrli/grades"))
				.andExpect(status().isOk())
				.andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
				.andExpect(jsonPath("$.length()").value(25))
				.andExpect(jsonPath("$[0].id").value(1))
				.andExpect(jsonPath("$[0].schoolPart").value("TBZ"))
				.andExpect(jsonPath("$[0].subject").value("Modul 450 Applikationen testen"))
				.andExpect(jsonPath("$[0].semester").value("HS 2026"))
				.andExpect(jsonPath("$[0].title").value("LB1 Testkonzept"))
				.andExpect(jsonPath("$[0].date").value("2026-09-30"))
				.andExpect(jsonPath("$[0].grade").value(5.5))
				.andExpect(jsonPath("$[0].weight").value(1.0))
				.andExpect(jsonPath("$[?(@.semester == 'FS 2026')]").isNotEmpty())
				.andExpect(jsonPath("$[?(@.schoolPart == 'BMS')]").isNotEmpty())
				.andExpect(jsonPath("$[?(@.schoolPart == 'UEK')]").isNotEmpty())
				.andExpect(jsonPath("$[?(@.title == 'Prüfung Trigonometrie')].grade").value(3.5));
	}

	@Test
	void shouldServeClassesOfMaxMuster() throws Exception {
		mockMvc.perform(get("/mock/users/max.muster/classes"))
				.andExpect(status().isOk())
				.andExpect(jsonPath("$.length()").value(1))
				.andExpect(jsonPath("$[0].id").value(7372))
				.andExpect(jsonPath("$[0].shortName").value("PE 26a"));
	}

	@Test
	void shouldAnswerUnknownUserWithProblemDetail() throws Exception {
		mockMvc.perform(get("/mock/users/erika.mustermann/grades"))
				.andExpect(status().isNotFound())
				.andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_PROBLEM_JSON))
				.andExpect(jsonPath("$.type").value("urn:betterintranet:school-mock:user-not-found"))
				.andExpect(jsonPath("$.title").value("User not found"))
				.andExpect(jsonPath("$.status").value(404))
				.andExpect(jsonPath("$.detail").value("No fixture data for user 'erika.mustermann'"));
	}

	@Test
	void shouldKeepActuatorHealthAvailable() throws Exception {
		mockMvc.perform(get("/actuator/health"))
				.andExpect(status().isOk())
				.andExpect(jsonPath("$.status").value("UP"));
	}
}
