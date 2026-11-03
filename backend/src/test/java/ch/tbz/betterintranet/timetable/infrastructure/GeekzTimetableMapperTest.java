package ch.tbz.betterintranet.timetable.infrastructure;

import ch.tbz.betterintranet.core.error.UpstreamUnavailableException;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import ch.tbz.betterintranet.timetable.TimetableFixtures;
import ch.tbz.betterintranet.timetable.api.Holiday;
import ch.tbz.betterintranet.timetable.api.Lesson;
import ch.tbz.betterintranet.timetable.api.LessonStatus;
import ch.tbz.betterintranet.timetable.api.SchoolClass;
import ch.tbz.betterintranet.timetable.infrastructure.GeekzEntriesResponse.Day;
import ch.tbz.betterintranet.timetable.infrastructure.GeekzEntriesResponse.Duration;
import ch.tbz.betterintranet.timetable.infrastructure.GeekzEntriesResponse.GridEntry;
import ch.tbz.betterintranet.timetable.infrastructure.GeekzEntriesResponse.Position;
import ch.tbz.betterintranet.timetable.infrastructure.GeekzEntriesResponse.Resource;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.Arrays;
import java.util.List;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import tools.jackson.databind.json.JsonMapper;

class GeekzTimetableMapperTest {

    private static final JsonMapper JSON = JsonMapper.builder().build();
    private static final LocalDate TUESDAY = LocalDate.of(2026, 11, 3);

    private final GeekzTimetableMapper mapper = new GeekzTimetableMapper();

    @Nested
    class Lessons {

        private final GeekzEntriesResponse recorded =
                JSON.readValue(TimetableFixtures.read(TimetableFixtures.ENTRIES_AP24B), GeekzEntriesResponse.class);

        @Test
        @DisplayName("the recorded AP 24b week yields the four lessons of Tuesday 2026-11-03")
        void mapsRecordedWeek() {
            List<Lesson> lessons = mapper.toLessons(recorded);

            assertThat(lessons).hasSize(4);
            assertThat(lessons.get(0)).isEqualTo(new Lesson(TUESDAY, LocalTime.of(8, 10), LocalTime.of(9, 45),
                    "ENG", "Englisch", List.of("MID"), List.of("AU-512"), LessonStatus.REGULAR, "oB"));
            assertThat(lessons.get(1)).isEqualTo(new Lesson(TUESDAY, LocalTime.of(10, 5), LocalTime.of(12, 30),
                    "GES", "Allgemeinbildung Gesellschaft", List.of("RIA"), List.of("AU-202"), LessonStatus.REGULAR, "oB"));
            assertThat(lessons.get(2)).isEqualTo(new Lesson(TUESDAY, LocalTime.of(13, 25), LocalTime.of(15, 0),
                    "450", "450 Applikationen testen", List.of("VAJ"), List.of("AU-060"), LessonStatus.REGULAR, "bili"));
            assertThat(lessons.get(3).start()).isEqualTo(LocalTime.of(15, 20));
        }

        @Test
        @DisplayName("days with status NO_DATA produce no lessons")
        void skipsNoDataDays() {
            List<Lesson> lessons = mapper.toLessons(recorded);

            assertThat(lessons).extracting(Lesson::date).containsOnly(TUESDAY);
        }

        @Test
        @DisplayName("empty or missing position arrays give no teachers or rooms and a null subject")
        void toleratesEmptyPositions() {
            GridEntry entry = new GridEntry(duration(8, 10, 9, 45), "REGULAR", List.of(), null, List.of(), null);
            GeekzEntriesResponse response = new GeekzEntriesResponse(List.of(day("REGULAR", List.of(entry))), null);

            Lesson lesson = mapper.toLessons(response).getFirst();

            assertThat(lesson.teachers()).isEmpty();
            assertThat(lesson.subject()).isNull();
            assertThat(lesson.subjectName()).isNull();
            assertThat(lesson.rooms()).isEmpty();
            assertThat(lesson.info()).isNull();
        }

        @Test
        @DisplayName("a day without gridEntries and a NOT_FOUND answer both yield no lessons")
        void toleratesMissingDaysAndEntries() {
            assertThat(mapper.toLessons(new GeekzEntriesResponse(List.of(day("REGULAR", null)), null))).isEmpty();
            assertThat(mapper.toLessons(new GeekzEntriesResponse(null, GeekzEntriesResponse.NOT_FOUND))).isEmpty();
        }

        @Test
        @DisplayName("team teaching keeps every teacher and every room in upstream order")
        void keepsAllTeachersAndRooms() {
            GridEntry abu = new GridEntry(duration(10, 5, 12, 30), "REGULAR",
                    List.of(position("ARS"), position("LUT")),
                    List.of(position("GES")),
                    List.of(position("AU-218"), position("AU-219")), null);
            GeekzEntriesResponse response = new GeekzEntriesResponse(List.of(day("REGULAR", List.of(abu))), null);

            Lesson lesson = mapper.toLessons(response).getFirst();

            assertThat(lesson.teachers()).containsExactly("ARS", "LUT");
            assertThat(lesson.rooms()).containsExactly("AU-218", "AU-219");
        }

        @Test
        @DisplayName("an errorCode other than NOT_FOUND is an upstream failure, not an empty week")
        void otherErrorCodeIsUpstreamFailure() {
            GeekzEntriesResponse response = new GeekzEntriesResponse(null, "INTERNAL_ERROR");

            assertThatThrownBy(() -> mapper.toLessons(response))
                    .isInstanceOf(UpstreamUnavailableException.class)
                    .hasMessageContaining("INTERNAL_ERROR");
        }

        @Test
        @DisplayName("a lesson without duration is an upstream failure instead of a NullPointerException")
        void missingDurationIsUpstreamFailure() {
            GridEntry broken = new GridEntry(null, "REGULAR", List.of(), List.of(), List.of(), null);
            GeekzEntriesResponse response = new GeekzEntriesResponse(List.of(day("REGULAR", List.of(broken))), null);

            assertThatThrownBy(() -> mapper.toLessons(response))
                    .isInstanceOf(UpstreamUnavailableException.class)
                    .hasMessageContaining("duration");
        }

        @Test
        @DisplayName("the upstream grid status is carried into the lesson")
        void carriesStatus() {
            GridEntry cancelled = new GridEntry(duration(8, 10, 9, 45), "CANCELLED", List.of(), List.of(), List.of(), null);
            GeekzEntriesResponse response = new GeekzEntriesResponse(List.of(day("REGULAR", List.of(cancelled))), null);

            assertThat(mapper.toLessons(response).getFirst().status()).isEqualTo(LessonStatus.CANCELLED);
        }

        private Position position(String shortName) {
            return new Position(new GeekzEntriesResponse.Resource(shortName, shortName));
        }

        private Day day(String status, List<GridEntry> entries) {
            return new Day(TUESDAY, status, entries);
        }

        private Duration duration(int fromHour, int fromMinute, int toHour, int toMinute) {
            return new Duration(LocalDateTime.of(TUESDAY, LocalTime.of(fromHour, fromMinute)),
                    LocalDateTime.of(TUESDAY, LocalTime.of(toHour, toMinute)));
        }
    }

    @Nested
    class Status {

        @ParameterizedTest(name = "{0} -> {1}")
        @CsvSource({
                "REGULAR, REGULAR",
                "CANCELLED, CANCELLED",
                "SUBSTITUTION, CHANGED",
                "CHANGED, CHANGED",
                "MOVED, CHANGED",
                "SOMETHING_NEW, CHANGED"
        })
        void mapsUpstreamStatus(String upstream, LessonStatus expected) {
            assertThat(mapper.toStatus(upstream)).isEqualTo(expected);
        }

        @Test
        @DisplayName("a missing status means nothing special happened")
        void missingStatusIsRegular() {
            assertThat(mapper.toStatus(null)).isEqualTo(LessonStatus.REGULAR);
        }
    }

    @Nested
    class Classes {

        @Test
        @DisplayName("the recorded filter answer yields every class with id, names and department")
        void mapsRecordedClasses() {
            GeekzClassesResponse response =
                    JSON.readValue(TimetableFixtures.read(TimetableFixtures.CLASSES), GeekzClassesResponse.class);

            List<SchoolClass> classes = mapper.toSchoolClasses(response);

            assertThat(classes).hasSize(11)
                    .contains(new SchoolClass(7471, "AP 24b", "Informatiker/innen Applikationsentwicklung", "IT"))
                    .contains(new SchoolClass(7898, "EXPE 26", "Elektro- und Sicherheitsexperten", "EE"));
        }

        @Test
        @DisplayName("a class without department has a null department")
        void toleratesMissingDepartment() {
            GeekzClassesResponse response = new GeekzClassesResponse(List.of(
                    new GeekzClassesResponse.ClassEntry(new GeekzClassesResponse.Resource(1, "X 1", "Test"), null)));

            assertThat(mapper.toSchoolClasses(response)).containsExactly(new SchoolClass(1, "X 1", "Test", null));
        }

        @Test
        @DisplayName("a filter answer without classes yields an empty list")
        void toleratesMissingClasses() {
            assertThat(mapper.toSchoolClasses(new GeekzClassesResponse(null))).isEmpty();
        }
    }

    @Nested
    class Holidays {

        @Test
        @DisplayName("holidays use the long name and upstream aliases with identical name and dates are collapsed")
        void mapsRecordedHolidays() {
            List<GeekzHolidayResponse> response = Arrays.asList(
                    JSON.readValue(TimetableFixtures.read(TimetableFixtures.HOLIDAYS), GeekzHolidayResponse[].class));

            List<Holiday> holidays = mapper.toHolidays(response);

            assertThat(holidays).hasSize(15).doesNotHaveDuplicates()
                    .contains(new Holiday("Kantonale Lehrerkonferenz", LocalDate.of(2026, 11, 19), LocalDate.of(2026, 11, 19)))
                    .contains(new Holiday("Herbstferien", LocalDate.of(2026, 10, 5), LocalDate.of(2026, 10, 18)));
            assertThat(holidays).filteredOn(h -> h.name().equals("Herbstferien")).hasSize(1);
        }

        @Test
        @DisplayName("without a long name the short name is used")
        void fallsBackToShortName() {
            LocalDate day = LocalDate.of(2027, 5, 6);
            List<GeekzHolidayResponse> response = List.of(
                    new GeekzHolidayResponse("Auffahrt", null, day, day),
                    new GeekzHolidayResponse("Brücke", " ", day.plusDays(1), day.plusDays(2)));

            assertThat(mapper.toHolidays(response)).containsExactly(
                    new Holiday("Auffahrt", day, day),
                    new Holiday("Brücke", day.plusDays(1), day.plusDays(2)));
        }
    }
}
