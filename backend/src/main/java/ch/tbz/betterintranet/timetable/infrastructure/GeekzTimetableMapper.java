package ch.tbz.betterintranet.timetable.infrastructure;

import ch.tbz.betterintranet.core.error.UpstreamUnavailableException;
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
 *   <li>Grid entries: {@code position1} are the teachers, {@code position2} the subject, {@code position3} the
 *       rooms. Every teacher and room is kept (team teaching has two teachers); missing positions give empty
 *       lists and a {@code null} subject.</li>
 *   <li>Errors: {@code errorCode = NOT_FOUND} means there are no lessons; any other {@code errorCode}, and a
 *       lesson without date or duration, is an {@link UpstreamUnavailableException}.</li>
 *   <li>Status: {@code REGULAR} and {@code CANCELLED} map one to one, a missing status counts as regular, every
 *       other value ({@code SUBSTITUTION}, {@code MOVED}, ...) is reported as {@link LessonStatus#CHANGED}.</li>
 *   <li>Holidays: the upstream lists some holidays twice under a short alias (e.g. {@code Herbst} and
 *       {@code Herbstferien}); entries with identical long name and dates are collapsed into one.</li>
 * </ul>
 */
public class GeekzTimetableMapper {

    private static final String UPSTREAM = "timetable";
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
        if (GeekzEntriesResponse.NOT_FOUND.equals(response.errorCode())) {
            return List.of();
        }
        if (response.errorCode() != null) {
            throw new UpstreamUnavailableException(UPSTREAM, "entries answered with errorCode " + response.errorCode());
        }
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
        if (day.date() == null || entry.duration() == null
                || entry.duration().start() == null || entry.duration().end() == null) {
            throw new UpstreamUnavailableException(UPSTREAM,
                    "entries contain a lesson without date or duration on " + day.date());
        }
        GeekzEntriesResponse.Resource subject = all(entry.position2()).findFirst().orElse(null);
        return new Lesson(
                day.date(),
                entry.duration().start().toLocalTime(),
                entry.duration().end().toLocalTime(),
                subject == null ? null : subject.shortName(),
                subject == null ? null : subject.longName(),
                shortNames(entry.position1()),
                shortNames(entry.position3()),
                toStatus(entry.status()),
                entry.lessonInfo());
    }

    private static List<String> shortNames(List<Position> positions) {
        return all(positions)
                .map(GeekzEntriesResponse.Resource::shortName)
                .filter(Objects::nonNull)
                .toList();
    }

    private static Stream<GeekzEntriesResponse.Resource> all(List<Position> positions) {
        if (positions == null) {
            return Stream.empty();
        }
        return positions.stream()
                .map(Position::current)
                .filter(Objects::nonNull);
    }

    private static String holidayName(GeekzHolidayResponse holiday) {
        return holiday.longName() == null || holiday.longName().isBlank() ? holiday.name() : holiday.longName();
    }
}
