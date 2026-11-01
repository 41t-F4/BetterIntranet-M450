package ch.tbz.betterintranet.absences.api;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

class AbsenceStatusTest {

    @ParameterizedTest(name = "{0} is shown as \"{1}\"")
    @CsvSource({"OPEN, Offen", "EXCUSED, Entschuldigt", "UNEXCUSED, Unentschuldigt"})
    @DisplayName("every status has a German label for the UI")
    void hasGermanLabel(AbsenceStatus status, String expectedLabel) {
        assertThat(status.label()).isEqualTo(expectedLabel);
    }
}
