package ch.tbz.betterintranet.courses.infrastructure;

import ch.tbz.betterintranet.core.error.NotFoundException;
import ch.tbz.betterintranet.core.error.UpstreamUnavailableException;
import ch.tbz.betterintranet.courses.api.Course;
import ch.tbz.betterintranet.courses.application.CoursesClient;
import java.util.List;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestClient;
import org.springframework.web.client.RestClientException;

/**
 * {@link CoursesClient} backed by {@code GET {school.base-url}/mock/users/{username}/courses}. The upstream JSON
 * uses the same field names as {@link Course}, so it is deserialized directly into the api record.
 */
public class SchoolMockCoursesClient implements CoursesClient {

    static final String UPSTREAM = "school";

    private static final ParameterizedTypeReference<List<Course>> COURSE_LIST =
            new ParameterizedTypeReference<>() {
            };

    private final RestClient restClient;

    public SchoolMockCoursesClient(RestClient restClient) {
        this.restClient = restClient;
    }

    @Override
    public List<Course> coursesOf(String username) {
        try {
            List<Course> courses = restClient.get()
                    .uri("/mock/users/{username}/courses", username)
                    .retrieve()
                    .body(COURSE_LIST);
            return courses == null ? List.of() : List.copyOf(courses);
        } catch (HttpClientErrorException.NotFound e) {
            throw new NotFoundException("Unknown user '" + username + "'");
        } catch (RestClientException e) {
            throw new UpstreamUnavailableException(UPSTREAM, "could not load courses of '" + username + "'", e);
        }
    }
}
