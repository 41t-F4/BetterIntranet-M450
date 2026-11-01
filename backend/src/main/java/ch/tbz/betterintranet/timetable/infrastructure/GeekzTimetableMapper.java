package ch.tbz.betterintranet.timetable.infrastructure;

import ch.tbz.betterintranet.timetable.api.Holiday;
import ch.tbz.betterintranet.timetable.api.Lesson;
import ch.tbz.betterintranet.timetable.api.LessonStatus;
import ch.tbz.betterintranet.timetable.api.SchoolClass;
import java.util.List;

/** Translates the upstream JSON shapes into the module's public api records. */
public class GeekzTimetableMapper {

    public List<SchoolClass> toSchoolClasses(GeekzClassesResponse response) {
        throw new UnsupportedOperationException("not implemented");
    }

    public List<Lesson> toLessons(GeekzEntriesResponse response) {
        throw new UnsupportedOperationException("not implemented");
    }

    public List<Holiday> toHolidays(List<GeekzHolidayResponse> response) {
        throw new UnsupportedOperationException("not implemented");
    }

    public LessonStatus toStatus(String upstreamStatus) {
        throw new UnsupportedOperationException("not implemented");
    }
}
