package ch.tbz.betterintranet.user.infrastructure;

import ch.tbz.betterintranet.user.api.UserProfile;
import ch.tbz.betterintranet.user.application.UserProfileRepository;
import java.util.Optional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import tools.jackson.databind.ObjectMapper;

/** Reads the known users from {@code user/users.json} on the classpath. */
@Repository
public class ClasspathUserProfileRepository implements UserProfileRepository {

    public static final String DEFAULT_LOCATION = "user/users.json";

    @Autowired
    public ClasspathUserProfileRepository(ObjectMapper mapper) {
        this(mapper, DEFAULT_LOCATION);
    }

    ClasspathUserProfileRepository(ObjectMapper mapper, String location) {
    }

    @Override
    public Optional<UserProfile> findByUsername(String username) {
        throw new UnsupportedOperationException("not implemented");
    }
}
