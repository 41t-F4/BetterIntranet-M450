package ch.tbz.betterintranet.timetable.infrastructure;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import java.time.LocalDate;

/** One element of the {@code GET /holidays} array. */
@JsonIgnoreProperties(ignoreUnknown = true)
public record GeekzHolidayResponse(String name, String longName, LocalDate startDate, LocalDate endDate) {
}
