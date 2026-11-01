package ch.tbz.betterintranet.absences.infrastructure;

import ch.tbz.betterintranet.absences.api.Absence;
import ch.tbz.betterintranet.absences.api.AbsenceStatus;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;

/** One entry of {@code GET /mock/users/{username}/absences} as delivered by {@code school-mock}. */
record AbsenceDto(long id, LocalDate date, LocalTime lessonStart, LocalTime lessonEnd, String subject,
                  String teacher, AbsenceStatus status, String reason, LocalDateTime reportedAt) {

    Absence toAbsence() {
        return new Absence(id, date, lessonStart, lessonEnd, subject, teacher, status, reason, reportedAt);
    }
}
