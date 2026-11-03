package ch.tbz.schoolmock.repository;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.endsWith;
import static org.mockito.Mockito.atLeastOnce;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.lang.reflect.RecordComponent;
import java.nio.charset.StandardCharsets;
import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.springframework.core.io.DefaultResourceLoader;
import org.springframework.core.io.Resource;
import org.springframework.core.io.ResourceLoader;

import ch.tbz.schoolmock.api.Absence;
import ch.tbz.schoolmock.api.AbsenceStatus;
import ch.tbz.schoolmock.api.Course;
import ch.tbz.schoolmock.api.Grade;
import ch.tbz.schoolmock.api.SchoolClass;
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

	private static final List<String> NICOLA_SUBJECTS = List.of("Englisch", "Allgemeinbildung Gesellschaft",
			"450 Applikationen testen", "306 IT-Kleinprojekt abwickeln", "320 Objektorientiert programmieren",
			"322 Benutzerschnittstellen entwerfen und implementieren");

	private static final List<String> NICOLA_COURSE_CODES = List.of("ENG", "GES", "450", "306", "320", "322");

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
	@DisplayName("courses.json of nicola.wehrli contains only TBZ modules and Berufsschule subjects")
	void shouldReadTbzCoursesOfKnownUser() {
		List<Course> courses = repository.findCourses(NICOLA);

		assertThat(courses).hasSize(8);
		assertThat(courses).extracting(Course::code).allMatch(NICOLA_COURSE_CODES::contains);
		assertThat(courses).extracting(Course::semester).containsOnly("HS 2026", "FS 2026");
		assertThat(courses.getFirst()).isEqualTo(
				new Course("450-HS26", "450", "Applikationen testen", "VAJ", "HS 2026", 4, "AU-060"));
		assertThat(courses)
				.filteredOn(course -> course.code().equals("ENG") && course.semester().equals("HS 2026"))
				.singleElement()
				.satisfies(course -> {
					assertThat(course.name()).isEqualTo("Englisch");
					assertThat(course.teacher()).isEqualTo("MID");
					assertThat(course.room()).isEqualTo("AU-512");
					assertThat(course.lessonsPerWeek()).isEqualTo(2);
				});
	}

	@Test
	@DisplayName("course and grade records expose exactly the fields of the TBZ contract")
	void shouldExposeOnlyTbzFieldsOnCourseAndGradeRecords() {
		assertThat(Course.class.getRecordComponents())
				.extracting(RecordComponent::getName)
				.containsExactly("id", "code", "name", "teacher", "semester", "lessonsPerWeek", "room");
		assertThat(Grade.class.getRecordComponents())
				.extracting(RecordComponent::getName)
				.containsExactly("id", "subject", "semester", "title", "date", "grade", "weight");
	}

	@Test
	@DisplayName("absences.json dates, times, status enum and null reason are parsed")
	void shouldParseAbsenceDatesTimesAndStatus() {
		List<Absence> absences = repository.findAbsences(NICOLA);

		assertThat(absences).hasSize(8);
		assertThat(absences)
				.extracting(Absence::status)
				.contains(AbsenceStatus.OPEN, AbsenceStatus.EXCUSED, AbsenceStatus.UNEXCUSED);

		Absence first = absences.getFirst();
		assertThat(first.id()).isEqualTo(1L);
		assertThat(first.date()).isEqualTo(LocalDate.of(2026, 10, 20));
		assertThat(first.lessonStart()).isEqualTo(LocalTime.of(8, 10));
		assertThat(first.lessonEnd()).isEqualTo(LocalTime.of(9, 45));
		assertThat(first.subject()).isEqualTo("Englisch");
		assertThat(first.teacher()).isEqualTo("MID");
		assertThat(first.status()).isEqualTo(AbsenceStatus.OPEN);
		assertThat(first.reason()).isNull();
		assertThat(first.reportedAt()).isEqualTo(LocalDateTime.of(2026, 10, 20, 10, 0, 0));

		assertThat(absences).allSatisfy(absence -> {
			assertThat(absence.subject()).isIn(NICOLA_SUBJECTS);
			assertThat(absence.date().getDayOfWeek()).isEqualTo(DayOfWeek.TUESDAY);
			assertThat(absence.date()).isBetween(LocalDate.of(2026, 8, 18), LocalDate.of(2026, 10, 27));
		});
		assertThat(absences)
				.filteredOn(absence -> absence.status() != AbsenceStatus.EXCUSED)
				.allSatisfy(absence -> assertThat(absence.reason()).isNull());
		assertThat(absences)
				.filteredOn(absence -> absence.status() == AbsenceStatus.EXCUSED)
				.allSatisfy(absence -> assertThat(absence.reason()).isNotBlank());
	}

	@Test
	@DisplayName("grades.json is a flat list of Swiss grades of TBZ modules and Berufsschule subjects")
	void shouldReadGradesAsFlatList() {
		List<Grade> grades = repository.findGrades(NICOLA);

		assertThat(grades).hasSizeBetween(18, 22);
		assertThat(grades).extracting(Grade::semester).containsOnly("FS 2026", "HS 2026");
		assertThat(grades).extracting(Grade::subject).allMatch(NICOLA_SUBJECTS::contains);
		assertThat(grades).allSatisfy(grade -> {
			assertThat(grade.grade()).isBetween(1.0, 6.0);
			assertThat(grade.weight()).isNotNegative();
			assertThat(grade.date()).isNotNull();
		});
		assertThat(grades).anySatisfy(grade -> assertThat(grade.grade()).isLessThan(4.0));
		assertThat(grades)
				.filteredOn(grade -> grade.weight() == 0.0)
				.extracting(Grade::title)
				.containsExactly("Vokabeltest (freiwillig)");
		assertThat(grades)
				.filteredOn(grade -> grade.semester().equals("HS 2026"))
				.allSatisfy(grade -> {
					assertThat(grade.date().getDayOfWeek()).isEqualTo(DayOfWeek.TUESDAY);
					assertThat(grade.date()).isBetween(LocalDate.of(2026, 8, 25), LocalDate.of(2026, 10, 27));
				});
		assertThat(grades)
				.filteredOn(grade -> grade.semester().equals("FS 2026"))
				.allSatisfy(grade -> assertThat(grade.date())
						.isBetween(LocalDate.of(2026, 2, 17), LocalDate.of(2026, 6, 30)));
		assertThat(grades).extracting(Grade::subject).doesNotContain("306 IT-Kleinprojekt abwickeln");

		Grade first = grades.getFirst();
		assertThat(first.id()).isEqualTo(1L);
		assertThat(first.subject()).isEqualTo("450 Applikationen testen");
		assertThat(first.semester()).isEqualTo("HS 2026");
		assertThat(first.title()).isEqualTo("LB1 Testkonzept");
		assertThat(first.date()).isEqualTo(LocalDate.of(2026, 9, 29));
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
	@DisplayName("max.muster has TBZ courses, grades and absences only")
	void shouldReadTbzDataOfSecondUser() {
		List<String> subjects = List.of("117 Informatik- und Netzinfrastruktur", "Allgemeinbildung Gesellschaft",
				"Englisch");

		assertThat(repository.findCourses("max.muster")).hasSize(3)
				.extracting(Course::code)
				.containsExactly("117", "GES", "ENG");
		assertThat(repository.findGrades("max.muster")).hasSize(4)
				.extracting(Grade::subject)
				.allMatch(subjects::contains);
		assertThat(repository.findAbsences("max.muster")).hasSize(2)
				.extracting(Absence::subject)
				.allMatch(subjects::contains);
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

	@ParameterizedTest(name = "rejects \"{0}\" before touching the classpath")
	@ValueSource(strings = { "..", "a..b", ".hidden", "nicola.", "nicola..wehrli", "-nicola", "nicola-", "nicola._wehrli",
			"nicola/wehrli", "nicola\\wehrli", "nicola%2ewehrli", "Nicola.Wehrli" })
	@DisplayName("dots, dashes and underscores are only allowed as single separators between name parts")
	void shouldRejectUsernamesWithDotSequencesOrLeadingOrTrailingSeparators(String username) {
		ResourceLoader resourceLoader = spy(new DefaultResourceLoader());
		ClasspathUserFixtureRepository guarded = new ClasspathUserFixtureRepository(objectMapper, resourceLoader);

		assertThatThrownBy(() -> guarded.findGrades(username)).isInstanceOf(UserNotFoundException.class);

		verify(resourceLoader, times(0)).getResource(anyString());
	}

	@ParameterizedTest(name = "accepts \"{0}\" as a well-formed username")
	@ValueSource(strings = { "nicola.wehrli", "max.muster", "anna-lena.meier", "jan_2", "x" })
	@DisplayName("ordinary vorname.nachname usernames remain valid")
	void shouldAcceptOrdinaryUsernames(String username) {
		ResourceLoader resourceLoader = spy(new DefaultResourceLoader());
		ClasspathUserFixtureRepository guarded = new ClasspathUserFixtureRepository(objectMapper, resourceLoader);

		try {
			guarded.findGrades(username);
		}
		catch (UserNotFoundException expectedForUsersWithoutFixtures) {
			// only nicola.wehrli and max.muster have fixtures; the point is that the name was not rejected
		}

		verify(resourceLoader, atLeastOnce()).getResource(anyString());
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
