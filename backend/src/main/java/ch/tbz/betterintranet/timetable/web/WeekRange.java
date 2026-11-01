package ch.tbz.betterintranet.timetable.web;

import java.time.DayOfWeek;
import java.time.LocalDate;

/** Five school days starting on a given day; the default range of {@code GET /api/timetable}. */
record WeekRange(LocalDate from, LocalDate to) {

    private static final int SCHOOL_DAYS_AFTER_START = 4;

    /** The school week (Monday..Friday) that contains {@code day}; a weekend belongs to the week before it. */
    static WeekRange containing(LocalDate day) {
        return startingAt(day.with(DayOfWeek.MONDAY));
    }

    /** Five days beginning with {@code from}, e.g. Monday..Friday or Wednesday..Sunday. */
    static WeekRange startingAt(LocalDate from) {
        return new WeekRange(from, from.plusDays(SCHOOL_DAYS_AFTER_START));
    }
}
