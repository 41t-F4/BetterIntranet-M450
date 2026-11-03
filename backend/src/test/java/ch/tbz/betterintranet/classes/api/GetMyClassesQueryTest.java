package ch.tbz.betterintranet.classes.api;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.NullAndEmptySource;
import org.junit.jupiter.params.provider.ValueSource;

class GetMyClassesQueryTest {

    @Test
    @DisplayName("keeps the username as given")
    void keepsUsername() {
        assertThat(new GetMyClassesQuery("nicola.wehrli").username()).isEqualTo("nicola.wehrli");
    }

    @ParameterizedTest
    @NullAndEmptySource
    @ValueSource(strings = {"   "})
    @DisplayName("rejects a missing username")
    void rejectsBlankUsername(String username) {
        assertThatThrownBy(() -> new GetMyClassesQuery(username))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("username");
    }
}
