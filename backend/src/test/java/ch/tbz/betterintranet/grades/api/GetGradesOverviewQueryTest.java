package ch.tbz.betterintranet.grades.api;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.junit.jupiter.params.provider.NullAndEmptySource;
import org.junit.jupiter.params.provider.ValueSource;

class GetGradesOverviewQueryTest {

    @ParameterizedTest(name = "username ''{0}'' is rejected")
    @NullAndEmptySource
    @ValueSource(strings = {" ", "\t"})
    void shouldRejectBlankUsername(String username) {
        assertThatThrownBy(() -> new GetGradesOverviewQuery(username))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("username");
    }

    @Test
    @DisplayName("a regular username is kept as is")
    void shouldKeepUsername() {
        assertThat(new GetGradesOverviewQuery("nicola.wehrli").username()).isEqualTo("nicola.wehrli");
    }

    @ParameterizedTest(name = "{0} is labelled {1}")
    @CsvSource({"BMS, BMS", "TBZ, TBZ", "UEK, ÜK"})
    void shouldLabelSchoolPartsInGerman(SchoolPart part, String label) {
        assertThat(part.label()).isEqualTo(label);
    }
}
