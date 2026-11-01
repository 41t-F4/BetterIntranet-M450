package ch.tbz.betterintranet.grades.infrastructure;

import ch.tbz.betterintranet.grades.application.RawGrade;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import java.time.LocalDate;

/** One element of the JSON array returned by {@code GET /mock/users/{username}/grades}. */
@JsonIgnoreProperties(ignoreUnknown = true)
record SchoolGradeDto(long id, String subject, String semester, String title,
                      LocalDate date, double grade, double weight) {

    RawGrade toRawGrade() {
        return new RawGrade(id, subject, semester, title, date, grade, weight);
    }
}
