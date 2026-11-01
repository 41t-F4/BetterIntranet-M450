package ch.tbz.betterintranet.absences.domain;

import static ch.tbz.betterintranet.absences.api.AbsenceStatus.EXCUSED;
import static ch.tbz.betterintranet.absences.api.AbsenceStatus.OPEN;
import static ch.tbz.betterintranet.absences.api.AbsenceStatus.UNEXCUSED;
import static org.assertj.core.api.Assertions.assertThat;

import ch.tbz.betterintranet.absences.api.Absence;
import ch.tbz.betterintranet.absences.api.AbsenceStatus;
import ch.tbz.betterintranet.absences.api.AbsenceSummary;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

class AbsenceSummaryCalculatorTest {

    private final AbsenceSummaryCalculator calculator = new AbsenceSummaryCalculator();

    @Test
    @DisplayName("no absences give an all-zero summary")
    void emptyList() {
        AbsenceSummary summary = calculator.summarize(List.of());

        assertThat(summary).isEqualTo(new AbsenceSummary(0, 0, 0, 0, 0));
    }

    @Test
    @DisplayName("entries are counted per status and in total")
    void countsPerStatus() {
        List<Absence> absences = List.of(
                singleLesson(1, OPEN),
                singleLesson(2, EXCUSED),
                singleLesson(3, EXCUSED),
                singleLesson(4, UNEXCUSED),
                singleLesson(5, OPEN),
                singleLesson(6, EXCUSED));

        AbsenceSummary summary = calculator.summarize(absences);

        assertThat(summary.total()).isEqualTo(6);
        assertThat(summary.open()).isEqualTo(2);
        assertThat(summary.excused()).isEqualTo(3);
        assertThat(summary.unexcused()).isEqualTo(1);
    }

    @ParameterizedTest(name = "{0}-{1} counts as {2} lesson(s)")
    @CsvSource({
            "08:10, 08:55, 1",   // one 45-minute lesson
            "08:10, 09:45, 2",   // double lesson with the 5-minute break in between
            "08:10, 08:40, 1",   // left early: still one lesson
            "08:10, 09:10, 2",   // one hour overlaps two lessons
            "13:15, 15:35, 3",   // triple lesson incl. two 5-minute breaks
            "08:10, 08:10, 0",   // no duration
            "09:45, 08:10, 0"    // inconsistent upstream data is not counted negatively
    })
    @DisplayName("the duration is rounded up to whole 45-minute lessons")
    void countsLessonsOfOneAbsence(LocalTime start, LocalTime end, int expectedLessons) {
        Absence absence = new Absence(1, LocalDate.of(2026, 10, 12), start, end, "Englisch", "MID", OPEN, null,
                LocalDateTime.of(2026, 10, 12, 10, 0));

        assertThat(calculator.summarize(List.of(absence)).totalLessons()).isEqualTo(expectedLessons);
    }

    @Test
    @DisplayName("lessons of all entries are added up regardless of status")
    void sumsLessonsOverAllEntries() {
        List<Absence> absences = List.of(
                absence(1, LocalTime.of(8, 10), LocalTime.of(9, 45), OPEN),
                absence(2, LocalTime.of(10, 5), LocalTime.of(10, 50), EXCUSED),
                absence(3, LocalTime.of(13, 15), LocalTime.of(15, 35), UNEXCUSED));

        assertThat(calculator.summarize(absences).totalLessons()).isEqualTo(6);
    }

    private static Absence singleLesson(long id, AbsenceStatus status) {
        return absence(id, LocalTime.of(8, 10), LocalTime.of(8, 55), status);
    }

    private static Absence absence(long id, LocalTime start, LocalTime end, AbsenceStatus status) {
        return new Absence(id, LocalDate.of(2026, 10, 12), start, end, "Englisch", "MID", status, null,
                LocalDateTime.of(2026, 10, 12, 10, 0));
    }
}
