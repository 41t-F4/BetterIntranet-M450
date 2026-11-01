package ch.tbz.betterintranet.timetable.web;

import java.time.LocalDate;

/** Monday to Friday of one ISO week: the default range of {@code GET /api/timetable}. */
record WeekRange(LocalDate from, LocalDate to) {

    static WeekRange containing(LocalDate day) {
        throw new UnsupportedOperationException("not implemented");
    }
}
