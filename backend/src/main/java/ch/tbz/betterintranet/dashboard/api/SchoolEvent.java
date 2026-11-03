package ch.tbz.betterintranet.dashboard.api;

import java.time.LocalDate;

/**
 * One entry of "Termine der Schule", both dates inclusive.
 *
 * @param title     e.g. {@code Herbstferien} or {@code Elternabend}
 * @param startDate first day
 * @param endDate   last day, same as {@code startDate} for a single-day event
 * @param kind      whether this is a holiday from the timetable or a school event
 */
public record SchoolEvent(String title, LocalDate startDate, LocalDate endDate, EventKind kind) {
}
