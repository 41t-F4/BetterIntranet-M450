package ch.tbz.schoolmock.web;

import static org.hamcrest.Matchers.containsInAnyOrder;
import static org.hamcrest.Matchers.nullValue;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.io.IOException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import ch.tbz.schoolmock.api.Absence;
import ch.tbz.schoolmock.api.AbsenceStatus;
import ch.tbz.schoolmock.api.Course;
import ch.tbz.schoolmock.api.Grade;
import ch.tbz.schoolmock.api.SchoolClass;
import ch.tbz.schoolmock.repository.FixtureLoadException;
import ch.tbz.schoolmock.repository.UserFixtureRepository;
import ch.tbz.schoolmock.repository.UserNotFoundException;
import tools.jackson.databind.ObjectMapper;
import tools.jackson.databind.json.JsonMapper;

/**
 * Web-layer tests for {@link MockUserController} and {@link MockApiExceptionHandler}. The
 * repository is replaced by a Mockito mock, so these tests only cover routing, JSON shape and
 * error mapping.
 */
@WebMvcTest(MockUserController.class)
class MockUserControllerTest {

	private static final String NICOLA = "nicola.wehrli";

	private static final ObjectMapper JSON = JsonMapper.builder().build();

	@Autowired
	private MockMvc mockMvc;

	@MockitoBean
	private UserFixtureRepository repository;

	@Test
	void shouldReturnClassesOfUserAsJson() throws Exception {
		when(repository.findClasses(NICOLA)).thenReturn(List.of(new SchoolClass(7471, "AP 24b",
				"Informatiker/innen Applikationsentwicklung", "IT", "MID", "2026/27", "STUDENT")));

		mockMvc.perform(get("/mock/users/{username}/classes", NICOLA))
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
	void shouldReturnCoursesOfUserAsJson() throws Exception {
		when(repository.findCourses(NICOLA)).thenReturn(List.of(
				fromJson(Course.class, """
						{ "id": "450-HS26", "code": "450", "name": "Applikationen testen", "teacher": "VAJ",
						  "semester": "HS 2026", "lessonsPerWeek": 4, "room": "AU-060" }"""),
				fromJson(Course.class, """
						{ "id": "ENG-HS26", "code": "ENG", "name": "Englisch", "teacher": "MID",
						  "semester": "HS 2026", "lessonsPerWeek": 2, "room": "AU-512" }""")));

		mockMvc.perform(get("/mock/users/{username}/courses", NICOLA))
				.andExpect(status().isOk())
				.andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
				.andExpect(jsonPath("$.length()").value(2))
				.andExpect(jsonPath("$[0].id").value("450-HS26"))
				.andExpect(jsonPath("$[0].code").value("450"))
				.andExpect(jsonPath("$[0].name").value("Applikationen testen"))
				.andExpect(jsonPath("$[0].teacher").value("VAJ"))
				.andExpect(jsonPath("$[0].semester").value("HS 2026"))
				.andExpect(jsonPath("$[0].lessonsPerWeek").value(4))
				.andExpect(jsonPath("$[0].room").value("AU-060"))
				.andExpect(jsonPath("$[0].schoolPart").doesNotExist())
				.andExpect(jsonPath("$[0].keys()", containsInAnyOrder("id", "code", "name", "teacher", "semester",
						"lessonsPerWeek", "room")))
				.andExpect(jsonPath("$[1].code").value("ENG"))
				.andExpect(jsonPath("$[1].name").value("Englisch"));
	}

	@Test
	void shouldReturnAbsencesWithIsoDatesAndShortTimes() throws Exception {
		when(repository.findAbsences(NICOLA)).thenReturn(List.of(
				new Absence(1L, LocalDate.of(2026, 10, 20), LocalTime.of(8, 10), LocalTime.of(9, 45), "Englisch",
						"MID", AbsenceStatus.OPEN, null, LocalDateTime.of(2026, 10, 20, 10, 0)),
				new Absence(3L, LocalDate.of(2026, 9, 15), LocalTime.of(10, 5), LocalTime.of(12, 30),
						"Allgemeinbildung Gesellschaft", "RIA", AbsenceStatus.EXCUSED, "Arztbesuch",
						LocalDateTime.of(2026, 9, 15, 12, 45))));

		mockMvc.perform(get("/mock/users/{username}/absences", NICOLA))
				.andExpect(status().isOk())
				.andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
				.andExpect(jsonPath("$.length()").value(2))
				.andExpect(jsonPath("$[0].id").value(1))
				.andExpect(jsonPath("$[0].date").value("2026-10-20"))
				.andExpect(jsonPath("$[0].lessonStart").value("08:10"))
				.andExpect(jsonPath("$[0].lessonEnd").value("09:45"))
				.andExpect(jsonPath("$[0].subject").value("Englisch"))
				.andExpect(jsonPath("$[0].teacher").value("MID"))
				.andExpect(jsonPath("$[0].status").value("OPEN"))
				.andExpect(jsonPath("$[0].reason").value(nullValue()))
				.andExpect(jsonPath("$[0].reportedAt").value("2026-10-20T10:00:00"))
				.andExpect(jsonPath("$[1].status").value("EXCUSED"))
				.andExpect(jsonPath("$[1].reason").value("Arztbesuch"));
	}

	@Test
	void shouldReturnGradesOfUserAsJson() throws Exception {
		when(repository.findGrades(NICOLA)).thenReturn(List.of(
				fromJson(Grade.class, """
						{ "id": 1, "subject": "450 Applikationen testen", "semester": "HS 2026",
						  "title": "LB1 Testkonzept", "date": "2026-09-29", "grade": 5.5, "weight": 1.0 }"""),
				fromJson(Grade.class, """
						{ "id": 9, "subject": "Allgemeinbildung Gesellschaft", "semester": "HS 2026",
						  "title": "Prüfung Recht und Vertrag", "date": "2026-10-27", "grade": 3.5, "weight": 1.0 }""")));

		mockMvc.perform(get("/mock/users/{username}/grades", NICOLA))
				.andExpect(status().isOk())
				.andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
				.andExpect(jsonPath("$.length()").value(2))
				.andExpect(jsonPath("$[0].id").value(1))
				.andExpect(jsonPath("$[0].schoolPart").doesNotExist())
				.andExpect(jsonPath("$[0].keys()", containsInAnyOrder("id", "subject", "semester", "title", "date",
						"grade", "weight")))
				.andExpect(jsonPath("$[0].subject").value("450 Applikationen testen"))
				.andExpect(jsonPath("$[0].semester").value("HS 2026"))
				.andExpect(jsonPath("$[0].title").value("LB1 Testkonzept"))
				.andExpect(jsonPath("$[0].date").value("2026-09-29"))
				.andExpect(jsonPath("$[0].grade").value(5.5))
				.andExpect(jsonPath("$[0].weight").value(1.0))
				.andExpect(jsonPath("$[1].grade").value(3.5))
				.andExpect(jsonPath("$[1].title").value("Prüfung Recht und Vertrag"));
	}

	@Test
	void shouldPassPathUsernameToRepository() throws Exception {
		when(repository.findClasses(anyString())).thenReturn(List.of());

		mockMvc.perform(get("/mock/users/{username}/classes", "max.muster"))
				.andExpect(status().isOk())
				.andExpect(jsonPath("$.length()").value(0));

		verify(repository).findClasses("max.muster");
	}

	@ParameterizedTest(name = "GET /mock/users/unknown.user/{0} -> 404 problem detail")
	@ValueSource(strings = { "classes", "courses", "absences", "grades" })
	void shouldReturn404ProblemDetailWhenUserIsUnknown(String resource) throws Exception {
		UserNotFoundException notFound = new UserNotFoundException("unknown.user");
		when(repository.findClasses("unknown.user")).thenThrow(notFound);
		when(repository.findCourses("unknown.user")).thenThrow(notFound);
		when(repository.findAbsences("unknown.user")).thenThrow(notFound);
		when(repository.findGrades("unknown.user")).thenThrow(notFound);

		mockMvc.perform(get("/mock/users/unknown.user/" + resource))
				.andExpect(status().isNotFound())
				.andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_PROBLEM_JSON))
				.andExpect(jsonPath("$.type").value("urn:betterintranet:school-mock:user-not-found"))
				.andExpect(jsonPath("$.title").value("User not found"))
				.andExpect(jsonPath("$.status").value(404))
				.andExpect(jsonPath("$.detail").value("No fixture data for user 'unknown.user'"))
				.andExpect(jsonPath("$.instance").value("/mock/users/unknown.user/" + resource));
	}

	@Test
	void shouldReturn500ProblemDetailWhenFixturesAreBroken() throws Exception {
		when(repository.findGrades(NICOLA))
				.thenThrow(new FixtureLoadException("Cannot read grades.json", new IOException("boom")));

		mockMvc.perform(get("/mock/users/{username}/grades", NICOLA))
				.andExpect(status().isInternalServerError())
				.andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_PROBLEM_JSON))
				.andExpect(jsonPath("$.type").value("urn:betterintranet:school-mock:fixture-unreadable"))
				.andExpect(jsonPath("$.title").value("Fixture data unreadable"))
				.andExpect(jsonPath("$.status").value(500))
				.andExpect(jsonPath("$.detail").value("Cannot read grades.json"));
	}

	private static <T> T fromJson(Class<T> type, String json) {
		return JSON.readValue(json, type);
	}
}
