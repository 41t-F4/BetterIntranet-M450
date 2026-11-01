package ch.tbz.schoolmock.repository;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.endsWith;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.core.io.DefaultResourceLoader;
import org.springframework.core.io.Resource;
import org.springframework.core.io.ResourceLoader;

import ch.tbz.schoolmock.api.Absence;
import ch.tbz.schoolmock.api.AbsenceStatus;
import ch.tbz.schoolmock.api.Course;
import ch.tbz.schoolmock.api.Grade;
import ch.tbz.schoolmock.api.SchoolClass;
import ch.tbz.schoolmock.api.SchoolPart;
import tools.jackson.core.JacksonException;
import tools.jackson.databind.ObjectMapper;
import tools.jackson.databind.json.JsonMapper;

/**
 * Unit tests for {@link ClasspathUserFixtureRepository} against the real fixture files on the
 * classpath. The resource loader is spied or mocked where the behaviour under test is caching or
 * error handling rather than parsing.
 */
class ClasspathUserFixtureRepositoryTest {

	private static final String NICOLA = "nicola.wehrli";

	private final ObjectMapper objectMapper = JsonMapper.builder().build();

	private final ClasspathUserFixtureRepository repository = new ClasspathUserFixtureRepository(objectMapper,
			new DefaultResourceLoader());

	@Test
	@DisplayName("classes.json of nicola.wehrli is mapped to a SchoolClass record")
	void shouldReadClassesOfKnownUser() {
		List<SchoolClass> classes = repository.findClasses(NICOLA);

		assertThat(classes).hasSize(1);
		SchoolClass schoolClass = classes.getFirst();
		assertThat(schoolClass.id()).isEqualTo(7471);
		assertThat(schoolClass.shortName()).isEqualTo("AP 24b");
		assertThat(schoolClass.longName()).isEqualTo("Informatiker/innen Applikationsentwicklung");
		assertThat(schoolClass.department()).isEqualTo("IT");
		assertThat(schoolClass.classTeacher()).isEqualTo("MID");
		assertThat(schoolClass.schoolYear()).isEqualTo("2026/27");
		assertThat(schoolClass.role()).isEqualTo("STUDENT");
	}

	@Test
	@DisplayName("courses.json is mapped including the SchoolPart enum")
	void shouldReadCoursesAndMapSchoolPart() {
		List<Course> courses = repository.findCourses(NICOLA);

		assertThat(courses)
				.extracting(Course::schoolPart)
				.contains(SchoolPart.TBZ, SchoolPart.BMS, SchoolPart.UEK);
		assertThat(courses)
				.filteredOn(course -> course.id().equals("M450"))
				.singleElement()
				.satisfies(course -> {
					assertThat(course.code()).isEqualTo("M450");
					assertThat(course.name()).isEqualTo("Applikationen testen");
					assertThat(course.semester()).isEqualTo("HS 2026");
					assertThat(course.lessonsPerWeek()).isEqualTo(4);
					assertThat(course.room()).isEqualTo("AU-512");
					assertThat(course.schoolPart()).isEqualTo(SchoolPart.TBZ);
				});
	}

	@Test
	@DisplayName("absences.json dates, times, status enum and null reason are parsed")
	void shouldParseAbsenceDatesTimesAndStatus() {
		List<Absence> absences = repository.findAbsences(NICOLA);

		assertThat(absences).hasSizeBetween(6, 8);
		assertThat(absences)
				.extracting(Absence::status)
				.contains(AbsenceStatus.OPEN, AbsenceStatus.EXCUSED, AbsenceStatus.UNEXCUSED);

		Absence first = absences.getFirst();
		assertThat(first.id()).isEqualTo(1L);
		assertThat(first.date()).isEqualTo(LocalDate.of(2026, 10, 12));
		assertThat(first.lessonStart()).isEqualTo(LocalTime.of(8, 10));
		assertThat(first.lessonEnd()).isEqualTo(LocalTime.of(9, 45));
		assertThat(first.subject()).isEqualTo("Englisch");
		assertThat(first.teacher()).isEqualTo("MID");
		assertThat(first.status()).isEqualTo(AbsenceStatus.OPEN);
		assertThat(first.reason()).isNull();
		assertThat(first.reportedAt()).isEqualTo(LocalDateTime.of(2026, 10, 12, 10, 0, 0));

		assertThat(absences)
				.filteredOn(absence -> absence.status() == AbsenceStatus.EXCUSED)
				.allSatisfy(absence -> assertThat(absence.reason()).isNotBlank());
	}

	@Test
	@DisplayName("grades.json is a flat list of Swiss grades across semesters and school parts")
	void shouldReadGradesAsFlatList() {
		List<Grade> grades = repository.findGrades(NICOLA);

		assertThat(grades).hasSizeBetween(20, 25);
		assertThat(grades).extracting(Grade::semester).contains("FS 2026", "HS 2026");
		assertThat(grades).extracting(Grade::schoolPart).contains(SchoolPart.TBZ, SchoolPart.BMS, SchoolPart.UEK);
		assertThat(grades).allSatisfy(grade -> {
			assertThat(grade.grade()).isBetween(1.0, 6.0);
			assertThat(grade.weight()).isPositive();
			assertThat(grade.date()).isNotNull();
		});

		Grade first = grades.getFirst();
		assertThat(first.id()).isEqualTo(1L);
		assertThat(first.schoolPart()).isEqualTo(SchoolPart.TBZ);
		assertThat(first.subject()).isEqualTo("Modul 450 Applikationen testen");
		assertThat(first.semester()).isEqualTo("HS 2026");
		assertThat(first.title()).isEqualTo("LB1 Testkonzept");
		assertThat(first.date()).isEqualTo(LocalDate.of(2026, 9, 30));
		assertThat(first.grade()).isEqualTo(5.5);
		assertThat(first.weight()).isEqualTo(1.0);
	}

	@Test
	@DisplayName("fixtures of the second user are independent of the first one")
	void shouldReadFixturesOfSecondUser() {
		List<SchoolClass> classes = repository.findClasses("max.muster");

		assertThat(classes).singleElement().satisfies(schoolClass -> {
			assertThat(schoolClass.id()).isEqualTo(7372);
			assertThat(schoolClass.shortName()).isEqualTo("PE 26a");
		});
	}

	@Test
	void shouldThrowUserNotFoundExceptionForUnknownUser() {
		assertThatThrownBy(() -> repository.findClasses("unknown.user"))
				.isInstanceOf(UserNotFoundException.class)
				.hasMessageContaining("unknown.user")
				.extracting(exception -> ((UserNotFoundException) exception).getUsername())
				.isEqualTo("unknown.user");
	}

	@Test
	@DisplayName("usernames that are not plain identifiers never reach the resource loader")
	void shouldRejectUsernamesThatCouldEscapeTheFixtureFolder() {
		ResourceLoader resourceLoader = spy(new DefaultResourceLoader());
		ClasspathUserFixtureRepository guarded = new ClasspathUserFixtureRepository(objectMapper, resourceLoader);

		assertThatThrownBy(() -> guarded.findGrades("../application"))
				.isInstanceOf(UserNotFoundException.class);
		assertThatThrownBy(() -> guarded.findGrades(""))
				.isInstanceOf(UserNotFoundException.class);

		verify(resourceLoader, times(0)).getResource(anyString());
	}

	@Test
	@DisplayName("the four fixture files of a user are read once and then served from memory")
	void shouldLoadFixtureFilesOnlyOnceForTheSameUser() {
		ResourceLoader resourceLoader = spy(new DefaultResourceLoader());
		ClasspathUserFixtureRepository cached = new ClasspathUserFixtureRepository(objectMapper, resourceLoader);

		List<Grade> firstCall = cached.findGrades(NICOLA);
		List<Grade> secondCall = cached.findGrades(NICOLA);
		cached.findClasses(NICOLA);
		cached.findCourses(NICOLA);
		cached.findAbsences(NICOLA);

		assertThat(secondCall).isSameAs(firstCall);
		verify(resourceLoader, times(4)).getResource(anyString());
	}

	@Test
	void shouldWrapIoErrorsInFixtureLoadException() throws IOException {
		ResourceLoader resourceLoader = mock(ResourceLoader.class);
		Resource broken = mock(Resource.class);
		when(broken.exists()).thenReturn(true);
		when(broken.getInputStream()).thenThrow(new IOException("disk on fire"));
		when(resourceLoader.getResource(anyString())).thenReturn(broken);
		ClasspathUserFixtureRepository failing = new ClasspathUserFixtureRepository(objectMapper, resourceLoader);

		assertThatThrownBy(() -> failing.findClasses(NICOLA))
				.isInstanceOf(FixtureLoadException.class)
				.hasMessageContaining(NICOLA)
				.hasCauseInstanceOf(IOException.class);
	}

	@Test
	@DisplayName("a malformed fixture file is reported by name, other files of the user stay readable")
	void shouldWrapMalformedJsonInFixtureLoadException() throws IOException {
		ResourceLoader resourceLoader = spy(new DefaultResourceLoader());
		Resource malformed = mock(Resource.class);
		when(malformed.exists()).thenReturn(true);
		when(malformed.getInputStream())
				.thenReturn(new ByteArrayInputStream("this is not json".getBytes(StandardCharsets.UTF_8)));
		doReturn(malformed).when(resourceLoader).getResource(endsWith("/courses.json"));
		ClasspathUserFixtureRepository failing = new ClasspathUserFixtureRepository(objectMapper, resourceLoader);

		assertThatThrownBy(() -> failing.findCourses(NICOLA))
				.isInstanceOf(FixtureLoadException.class)
				.hasMessageContaining("courses.json")
				.hasMessageContaining(NICOLA)
				.hasCauseInstanceOf(JacksonException.class);
	}
}
