package ch.tbz.betterintranet.user.api;

import static org.assertj.core.api.Assertions.assertThatIllegalArgumentException;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.NullAndEmptySource;
import org.junit.jupiter.params.provider.ValueSource;

class GetUserProfileQueryTest {

    @ParameterizedTest
    @NullAndEmptySource
    @ValueSource(strings = {" ", "\t"})
    @DisplayName("a blank username is rejected when the query is built")
    void rejectsBlankUsername(String username) {
        assertThatIllegalArgumentException()
                .isThrownBy(() -> new GetUserProfileQuery(username))
                .withMessage("username must not be blank");
    }
}
