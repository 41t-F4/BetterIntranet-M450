package ch.tbz.betterintranet.classes.infrastructure;

import ch.tbz.betterintranet.classes.api.SchoolClassMembership;
import ch.tbz.betterintranet.classes.application.ClassesClient;
import ch.tbz.betterintranet.core.error.NotFoundException;
import ch.tbz.betterintranet.core.error.UpstreamUnavailableException;
import java.util.List;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestClient;
import org.springframework.web.client.RestClientException;

/**
 * {@link ClassesClient} backed by {@code GET {school.base-url}/mock/users/{username}/classes}. The upstream JSON
 * uses the same field names as {@link SchoolClassMembership}, so it is deserialized directly into the api record.
 */
public class SchoolMockClassesClient implements ClassesClient {

    private static final String UPSTREAM = "school";

    private static final ParameterizedTypeReference<List<SchoolClassMembership>> CLASS_LIST =
            new ParameterizedTypeReference<>() {
            };

    private final RestClient restClient;

    public SchoolMockClassesClient(RestClient restClient) {
        this.restClient = restClient;
    }

    @Override
    public List<SchoolClassMembership> classesOf(String username) {
        try {
            List<SchoolClassMembership> classes = restClient.get()
                    .uri("/mock/users/{username}/classes", username)
                    .retrieve()
                    .body(CLASS_LIST);
            return classes == null ? List.of() : List.copyOf(classes);
        } catch (HttpClientErrorException.NotFound e) {
            throw new NotFoundException("Unknown user '" + username + "'");
        } catch (RestClientException e) {
            throw new UpstreamUnavailableException(UPSTREAM, "could not load classes of '" + username + "'", e);
        }
    }
}
