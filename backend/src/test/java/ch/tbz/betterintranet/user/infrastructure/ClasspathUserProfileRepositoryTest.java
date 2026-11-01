package ch.tbz.betterintranet.user.infrastructure;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatIllegalStateException;

import ch.tbz.betterintranet.user.api.UserProfile;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import tools.jackson.databind.ObjectMapper;
import tools.jackson.databind.json.JsonMapper;

class ClasspathUserProfileRepositoryTest {

    private final ObjectMapper mapper = JsonMapper.builder().build();
    private final ClasspathUserProfileRepository repository = new ClasspathUserProfileRepository(mapper);

    @Test
    @DisplayName("the default user nicola.wehrli is read from users.json with class AP 24b")
    void findsDefaultUser() {
        assertThat(repository.findByUsername("nicola.wehrli")).contains(new UserProfile(
                "nicola.wehrli", "Nicola Wehrli", "Nicola", "Wehrli", "nicola.wehrli@tbz.ch", "AP 24b", 7471, "STUDENT"));
    }

    @Test
    @DisplayName("max.muster is read from users.json with class PE 26a")
    void findsSecondUser() {
        assertThat(repository.findByUsername("max.muster")).hasValueSatisfying(profile -> {
            assertThat(profile.displayName()).isEqualTo("Max Muster");
            assertThat(profile.className()).isEqualTo("PE 26a");
            assertThat(profile.classId()).isEqualTo(7372L);
            assertThat(profile.role()).isEqualTo("STUDENT");
        });
    }

    @Test
    @DisplayName("an unknown username yields an empty result")
    void unknownUserIsEmpty() {
        assertThat(repository.findByUsername("unknown.person")).isEmpty();
    }

    @Test
    @DisplayName("fails at startup when users.json is missing")
    void failsFastWhenFileIsMissing() {
        assertThatIllegalStateException()
                .isThrownBy(() -> new ClasspathUserProfileRepository(mapper, "user/does-not-exist.json"))
                .withMessageContaining("user/does-not-exist.json");
    }

    @Test
    @DisplayName("fails at startup when users.json cannot be parsed")
    void failsFastWhenFileIsMalformed() {
        assertThatIllegalStateException()
                .isThrownBy(() -> new ClasspathUserProfileRepository(mapper, "user/malformed-users.json"))
                .withMessageContaining("user/malformed-users.json");
    }
}
