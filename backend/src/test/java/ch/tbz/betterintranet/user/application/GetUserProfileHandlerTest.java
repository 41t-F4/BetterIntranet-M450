package ch.tbz.betterintranet.user.application;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.when;

import ch.tbz.betterintranet.core.error.NotFoundException;
import ch.tbz.betterintranet.user.api.GetUserProfileQuery;
import ch.tbz.betterintranet.user.api.UserProfile;
import java.util.Optional;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class GetUserProfileHandlerTest {

    private static final UserProfile NICOLA = new UserProfile("nicola.wehrli", "Nicola Wehrli", "Nicola", "Wehrli",
            "nicola.wehrli@tbz.ch", "AP 24b", 7471, "STUDENT");

    @Mock
    private UserProfileRepository users;

    @InjectMocks
    private GetUserProfileHandler handler;

    @Test
    @DisplayName("returns the profile the repository knows for the username")
    void returnsKnownProfile() {
        when(users.findByUsername("nicola.wehrli")).thenReturn(Optional.of(NICOLA));

        UserProfile profile = handler.handle(new GetUserProfileQuery("nicola.wehrli"));

        assertThat(profile).isEqualTo(NICOLA);
    }

    @Test
    @DisplayName("an unknown username becomes a NotFoundException naming the user")
    void unknownUserIsNotFound() {
        when(users.findByUsername("unknown.person")).thenReturn(Optional.empty());

        assertThatThrownBy(() -> handler.handle(new GetUserProfileQuery("unknown.person")))
                .isInstanceOf(NotFoundException.class)
                .hasMessage("User unknown.person not found");
    }
}
