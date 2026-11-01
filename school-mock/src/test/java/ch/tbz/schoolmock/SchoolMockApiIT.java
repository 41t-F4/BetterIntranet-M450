package ch.tbz.schoolmock;

import static org.hamcrest.Matchers.containsInAnyOrder;
import static org.hamcrest.Matchers.everyItem;
import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.in;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.nullValue;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.util.List;

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

	private static final List<String> NICOLA_SUBJECTS = List.of("Englisch", "Allgemeinbildung Gesellschaft",
			"450 Applikationen testen", "306 IT-Kleinprojekt abwickeln", "320 Objektorientiert programmieren",
			"322 Benutzerschnittstellen entwerfen und implementieren");

	private static final List<String> NICOLA_COURSE_CODES = List.of("ENG", "GES", "450", "306", "320", "322");

	private static final List<String> MAX_SUBJECTS = List.of("117 Informatik- und Netzinfrastruktur",
			"Allgemeinbildung Gesellschaft", "Englisch");

	private static final String[] COURSE_FIELDS = { "id", "code", "name", "teacher", "semester", "lessonsPerWeek",
			"room" };

	private static final String[] GRADE_FIELDS = { "id", "subject", "semester", "title", "date", "grade", "weight" };

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
	void shouldServeTbzCoursesOfNicolaWehrli() throws Exception {
		mockMvc.perform(get("/mock/users/nicola.wehrli/courses"))
				.andExpect(status().isOk())
				.andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
				.andExpect(jsonPath("$.length()").value(8))
				.andExpect(jsonPath("$[0].id").value("450-HS26"))
				.andExpect(jsonPath("$[0].code").value("450"))
				.andExpect(jsonPath("$[0].name").value("Applikationen testen"))
				.andExpect(jsonPath("$[0].teacher").value("VAJ"))
				.andExpect(jsonPath("$[0].semester").value("HS 2026"))
				.andExpect(jsonPath("$[0].lessonsPerWeek").value(4))
				.andExpect(jsonPath("$[0].room").value("AU-060"))
				.andExpect(jsonPath("$[0].keys()", containsInAnyOrder(COURSE_FIELDS)))
				.andExpect(jsonPath("$[*].code", everyItem(is(in(NICOLA_COURSE_CODES)))))
				.andExpect(jsonPath("$[?(@.code == 'ENG' && @.semester == 'HS 2026')].room").value("AU-512"))
				.andExpect(jsonPath("$[?(@.code == 'GES' && @.semester == 'HS 2026')].teacher").value("RIA"))
				.andExpect(jsonPath("$[?(@.code == '306')].teacher").value("MUH"))
				.andExpect(jsonPath("$[?(@.semester == 'FS 2026')]", hasSize(4)));
	}

	@Test
	void shouldServeAbsencesOfNicolaWehrliWithAllStatuses() throws Exception {
		mockMvc.perform(get("/mock/users/nicola.wehrli/absences"))
				.andExpect(status().isOk())
				.andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
				.andExpect(jsonPath("$.length()").value(8))
				.andExpect(jsonPath("$[0].id").value(1))
				.andExpect(jsonPath("$[0].date").value("2026-10-20"))
				.andExpect(jsonPath("$[0].lessonStart").value("08:10"))
				.andExpect(jsonPath("$[0].lessonEnd").value("09:45"))
				.andExpect(jsonPath("$[0].subject").value("Englisch"))
				.andExpect(jsonPath("$[0].teacher").value("MID"))
				.andExpect(jsonPath("$[0].status").value("OPEN"))
				.andExpect(jsonPath("$[0].reason").value(nullValue()))
				.andExpect(jsonPath("$[0].reportedAt").value("2026-10-20T10:00:00"))
				.andExpect(jsonPath("$[*].subject", everyItem(is(in(NICOLA_SUBJECTS)))))
				.andExpect(jsonPath("$[*].teacher", everyItem(is(in(List.of("MID", "RIA", "VAJ"))))))
				.andExpect(jsonPath("$[?(@.status == 'OPEN')]").isNotEmpty())
				.andExpect(jsonPath("$[?(@.status == 'EXCUSED')]").isNotEmpty())
				.andExpect(jsonPath("$[?(@.status == 'UNEXCUSED')]").isNotEmpty())
				.andExpect(jsonPath("$[?(@.id == 3)].reason").value("Arztbesuch"));
	}

	@Test
	void shouldServeTbzGradesOfNicolaWehrliAsFlatList() throws Exception {
		mockMvc.perform(get("/mock/users/nicola.wehrli/grades"))
				.andExpect(status().isOk())
				.andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
				.andExpect(jsonPath("$.length()").value(19))
				.andExpect(jsonPath("$[0].id").value(1))
				.andExpect(jsonPath("$[0].keys()", containsInAnyOrder(GRADE_FIELDS)))
				.andExpect(jsonPath("$[0].subject").value("450 Applikationen testen"))
				.andExpect(jsonPath("$[0].semester").value("HS 2026"))
				.andExpect(jsonPath("$[0].title").value("LB1 Testkonzept"))
				.andExpect(jsonPath("$[0].date").value("2026-09-29"))
				.andExpect(jsonPath("$[0].grade").value(5.5))
				.andExpect(jsonPath("$[0].weight").value(1.0))
				.andExpect(jsonPath("$[*].subject", everyItem(is(in(NICOLA_SUBJECTS)))))
				.andExpect(jsonPath("$[?(@.semester == 'FS 2026')]").isNotEmpty())
				.andExpect(jsonPath("$[?(@.subject == '306 IT-Kleinprojekt abwickeln')]").isEmpty())
				.andExpect(jsonPath("$[?(@.title == 'Vokabeltest (freiwillig)')].weight").value(0.0))
				.andExpect(jsonPath("$[?(@.title == 'Prüfung Recht und Vertrag')].grade").value(3.5));
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
	void shouldServeTbzCoursesGradesAndAbsencesOfMaxMuster() throws Exception {
		mockMvc.perform(get("/mock/users/max.muster/courses"))
				.andExpect(status().isOk())
				.andExpect(jsonPath("$.length()").value(3))
				.andExpect(jsonPath("$[0].code").value("117"))
				.andExpect(jsonPath("$[0].name").value("Informatik- und Netzinfrastruktur"))
				.andExpect(jsonPath("$[0].teacher").value("CAL"))
				.andExpect(jsonPath("$[0].room").value("AU-U60"))
				.andExpect(jsonPath("$[0].keys()", containsInAnyOrder(COURSE_FIELDS)));
		mockMvc.perform(get("/mock/users/max.muster/grades"))
				.andExpect(status().isOk())
				.andExpect(jsonPath("$.length()").value(4))
				.andExpect(jsonPath("$[0].keys()", containsInAnyOrder(GRADE_FIELDS)))
				.andExpect(jsonPath("$[*].subject", everyItem(is(in(MAX_SUBJECTS)))));
		mockMvc.perform(get("/mock/users/max.muster/absences"))
				.andExpect(status().isOk())
				.andExpect(jsonPath("$.length()").value(2))
				.andExpect(jsonPath("$[*].subject", everyItem(is(in(MAX_SUBJECTS)))));
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
