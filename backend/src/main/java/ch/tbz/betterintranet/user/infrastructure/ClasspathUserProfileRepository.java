package ch.tbz.betterintranet.user.infrastructure;

import ch.tbz.betterintranet.user.api.UserProfile;
import ch.tbz.betterintranet.user.application.UserProfileRepository;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.function.Function;
import java.util.stream.Collectors;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Repository;
import tools.jackson.core.JacksonException;
import tools.jackson.core.type.TypeReference;
import tools.jackson.databind.ObjectMapper;

/**
 * Reads the known users once from {@code user/users.json} on the classpath. Stands in for the school's
 * SSO directory, which is out of scope.
 */
@Repository
public class ClasspathUserProfileRepository implements UserProfileRepository {

    public static final String DEFAULT_LOCATION = "user/users.json";

    private static final TypeReference<List<UserProfile>> USER_LIST = new TypeReference<>() {
    };

    private final Map<String, UserProfile> usersByUsername;

    @Autowired
    public ClasspathUserProfileRepository(ObjectMapper mapper) {
        this(mapper, DEFAULT_LOCATION);
    }

    ClasspathUserProfileRepository(ObjectMapper mapper, String location) {
        this.usersByUsername = load(mapper, location).stream()
                .collect(Collectors.toUnmodifiableMap(UserProfile::username, Function.identity()));
    }

    @Override
    public Optional<UserProfile> findByUsername(String username) {
        return Optional.ofNullable(usersByUsername.get(username));
    }

    private static List<UserProfile> load(ObjectMapper mapper, String location) {
        try (InputStream in = new ClassPathResource(location).getInputStream()) {
            return mapper.readValue(in, USER_LIST);
        } catch (IOException | JacksonException e) {
            throw new IllegalStateException("Cannot load users from classpath:" + location, e);
        }
    }
}
