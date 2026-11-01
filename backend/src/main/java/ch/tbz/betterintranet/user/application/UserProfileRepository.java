package ch.tbz.betterintranet.user.application;

import ch.tbz.betterintranet.user.api.UserProfile;
import java.util.Optional;

/** Port to the user store. Currently backed by a static JSON file, later by the school's SSO directory. */
public interface UserProfileRepository {

    Optional<UserProfile> findByUsername(String username);
}
