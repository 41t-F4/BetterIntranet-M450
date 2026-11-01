package ch.tbz.betterintranet.classes.infrastructure;

import ch.tbz.betterintranet.classes.api.SchoolClassMembership;
import ch.tbz.betterintranet.classes.application.ClassesClient;
import java.util.List;
import org.springframework.web.client.RestClient;

/** {@link ClassesClient} backed by {@code GET {school.base-url}/mock/users/{username}/classes}. */
public class SchoolMockClassesClient implements ClassesClient {

    private final RestClient restClient;

    public SchoolMockClassesClient(RestClient restClient) {
        this.restClient = restClient;
    }

    @Override
    public List<SchoolClassMembership> classesOf(String username) {
        throw new UnsupportedOperationException("not implemented");
    }
}
