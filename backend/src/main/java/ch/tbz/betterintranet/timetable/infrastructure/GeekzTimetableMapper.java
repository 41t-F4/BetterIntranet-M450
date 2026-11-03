package ch.tbz.betterintranet.timetable.infrastructure;

import ch.tbz.betterintranet.timetable.api.Holiday;
import ch.tbz.betterintranet.timetable.api.Lesson;
import ch.tbz.betterintranet.timetable.api.LessonStatus;
import ch.tbz.betterintranet.timetable.api.SchoolClass;
import ch.tbz.betterintranet.timetable.infrastructure.GeekzEntriesResponse.Day;
import ch.tbz.betterintranet.timetable.infrastructure.GeekzEntriesResponse.GridEntry;
import ch.tbz.betterintranet.timetable.infrastructure.GeekzEntriesResponse.Position;
import java.util.List;
import java.util.Objects;
import java.util.stream.Stream;

/**
 * Translates the upstream JSON shapes into the module's public api records.
 *
 * <ul>
 *   <li>Grid entries: {@code position1} is the teacher, {@code position2} the subject, {@code position3} the room.
 *       Empty or missing positions become {@code null}.</li>
 *   <li>Status: {@code REGULAR} and {@code CANCELLED} map one to one, a missing status counts as regular, every
 *       other value ({@code SUBSTITUTION}, {@code MOVED}, ...) is reported as {@link LessonStatus#CHANGED}.</li>
 *   <li>Holidays: the upstream lists some holidays twice under a short alias (e.g. {@code Herbst} and
 *       {@code Herbstferien}); entries with identical long name and dates are collapsed into one.</li>
 * </ul>
 */
public class GeekzTimetableMapper {

    private static final String REGULAR = "REGULAR";
    private static final String CANCELLED = "CANCELLED";

    public List<SchoolClass> toSchoolClasses(GeekzClassesResponse response) {
        if (response.classes() == null) {
            return List.of();
        }
        return response.classes().stream()
                .filter(entry -> entry.schoolClass() != null)
                .map(entry -> new SchoolClass(
                        entry.schoolClass().id(),
                        entry.schoolClass().shortName(),
                        entry.schoolClass().longName(),
                        entry.department() == null ? null : entry.department().shortName()))
                .toList();
    }

    public List<Lesson> toLessons(GeekzEntriesResponse response) {
        if (response.days() == null) {
            return List.of();
        }
        return response.days().stream()
                .flatMap(this::lessonsOf)
                .toList();
    }

    public List<Holiday> toHolidays(List<GeekzHolidayResponse> response) {
        return response.stream()
                .map(holiday -> new Holiday(holidayName(holiday), holiday.startDate(), holiday.endDate()))
                .distinct()
                .toList();
    }

    public LessonStatus toStatus(String upstreamStatus) {
        if (upstreamStatus == null || REGULAR.equals(upstreamStatus)) {
            return LessonStatus.REGULAR;
        }
        if (CANCELLED.equals(upstreamStatus)) {
            return LessonStatus.CANCELLED;
        }
        return LessonStatus.CHANGED;
    }

    private Stream<Lesson> lessonsOf(Day day) {
        if (day.gridEntries() == null) {
            return Stream.empty();
        }
        return day.gridEntries().stream().map(entry -> toLesson(day, entry));
    }

    private Lesson toLesson(Day day, GridEntry entry) {
        GeekzEntriesResponse.Resource teacher = first(entry.position1());
        GeekzEntriesResponse.Resource subject = first(entry.position2());
        GeekzEntriesResponse.Resource room = first(entry.position3());
        return new Lesson(
                day.date(),
                entry.duration().start().toLocalTime(),
                entry.duration().end().toLocalTime(),
                shortName(subject),
                subject == null ? null : subject.longName(),
                teacher == null ? List.of() : List.of(shortName(teacher)),
                room == null ? List.of() : List.of(shortName(room)),
                toStatus(entry.status()),
                entry.lessonInfo());
    }

    private static String shortName(GeekzEntriesResponse.Resource resource) {
        return resource == null ? null : resource.shortName();
    }

    private static GeekzEntriesResponse.Resource first(List<Position> positions) {
        if (positions == null) {
            return null;
        }
        return positions.stream()
                .map(Position::current)
                .filter(Objects::nonNull)
                .findFirst()
                .orElse(null);
    }

    private static String holidayName(GeekzHolidayResponse holiday) {
        return holiday.longName() == null || holiday.longName().isBlank() ? holiday.name() : holiday.longName();
    }
}
