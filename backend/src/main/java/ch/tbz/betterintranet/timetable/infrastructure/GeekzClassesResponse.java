package ch.tbz.betterintranet.timetable.infrastructure;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.List;

/** Shape of {@code GET /api/rest/view/v1/timetable/filter?resourceType=CLASS} as far as this module needs it. */
@JsonIgnoreProperties(ignoreUnknown = true)
public record GeekzClassesResponse(List<ClassEntry> classes) {

    /** One class with its department; {@code department} may be null upstream. */
    @JsonIgnoreProperties(ignoreUnknown = true)
    public record ClassEntry(@JsonProperty("class") Resource schoolClass, Resource department) {
    }

    @JsonIgnoreProperties(ignoreUnknown = true)
    public record Resource(long id, String shortName, String longName) {
    }
}
