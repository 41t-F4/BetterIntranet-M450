package ch.tbz.betterintranet.timetable.api;

import java.time.LocalDate;

/** A school holiday or closed day, both dates inclusive. */
public record Holiday(String name, LocalDate startDate, LocalDate endDate) {
}
