package ch.tbz.betterintranet.timetable.infrastructure;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

/**
 * Shape of {@code GET /api/rest/view/v1/timetable/entries?format=5} as far as this module needs it. When there is
 * no data for the requested period the upstream answers with {@code errorCode = "NOT_FOUND"} instead of
 * {@code days}.
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public record GeekzEntriesResponse(List<Day> days, String errorCode) {

    public static final String NOT_FOUND = "NOT_FOUND";

    /** One calendar day; {@code status} is {@code REGULAR} or {@code NO_DATA}. */
    @JsonIgnoreProperties(ignoreUnknown = true)
    public record Day(LocalDate date, String status, List<GridEntry> gridEntries) {
    }

    /** One block in the grid: position1 = teacher, position2 = subject, position3 = room. */
    @JsonIgnoreProperties(ignoreUnknown = true)
    public record GridEntry(Duration duration,
                            String status,
                            List<Position> position1,
                            List<Position> position2,
                            List<Position> position3,
                            String lessonInfo) {
    }

    @JsonIgnoreProperties(ignoreUnknown = true)
    public record Duration(LocalDateTime start, LocalDateTime end) {
    }

    @JsonIgnoreProperties(ignoreUnknown = true)
    public record Position(Resource current) {
    }

    @JsonIgnoreProperties(ignoreUnknown = true)
    public record Resource(String shortName, String longName) {
    }
}
