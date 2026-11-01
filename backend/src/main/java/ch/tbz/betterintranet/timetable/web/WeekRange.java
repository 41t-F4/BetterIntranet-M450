package ch.tbz.betterintranet.timetable.web;

import java.time.DayOfWeek;
import java.time.LocalDate;

/** Monday to Friday of one ISO week: the default range of {@code GET /api/timetable}. */
record WeekRange(LocalDate from, LocalDate to) {

    private static final int SCHOOL_DAYS_AFTER_MONDAY = 4;

    /** The school week (Monday..Friday) that contains {@code day}; a weekend belongs to the week before it. */
    static WeekRange containing(LocalDate day) {
        LocalDate monday = day.with(DayOfWeek.MONDAY);
        return new WeekRange(monday, monday.plusDays(SCHOOL_DAYS_AFTER_MONDAY));
    }
}
