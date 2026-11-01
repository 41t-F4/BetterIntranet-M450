package ch.tbz.betterintranet.absences.api;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.NullAndEmptySource;
import org.junit.jupiter.params.provider.ValueSource;

class GetAbsencesQueryTest {

    @Test
    @DisplayName("without a status the query asks for all absences")
    void defaultsToAllStatuses() {
        var query = new GetAbsencesQuery("nicola.wehrli");

        assertThat(query.username()).isEqualTo("nicola.wehrli");
        assertThat(query.status()).isNull();
    }

    @Test
    @DisplayName("a status narrows the query")
    void keepsStatusFilter() {
        assertThat(new GetAbsencesQuery("nicola.wehrli", AbsenceStatus.OPEN).status()).isEqualTo(AbsenceStatus.OPEN);
    }

    @ParameterizedTest
    @NullAndEmptySource
    @ValueSource(strings = {"   "})
    @DisplayName("a blank username is rejected")
    void rejectsBlankUsername(String username) {
        assertThatThrownBy(() -> new GetAbsencesQuery(username))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("username");
    }
}
