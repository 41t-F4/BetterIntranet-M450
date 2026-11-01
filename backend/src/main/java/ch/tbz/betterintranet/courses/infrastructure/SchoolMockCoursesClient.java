package ch.tbz.betterintranet.courses.infrastructure;

import ch.tbz.betterintranet.courses.api.Course;
import ch.tbz.betterintranet.courses.application.CoursesClient;
import java.util.List;
import org.springframework.web.client.RestClient;

/** {@link CoursesClient} backed by {@code GET {school.base-url}/mock/users/{username}/courses}. */
public class SchoolMockCoursesClient implements CoursesClient {

    private final RestClient restClient;

    public SchoolMockCoursesClient(RestClient restClient) {
        this.restClient = restClient;
    }

    @Override
    public List<Course> coursesOf(String username) {
        throw new UnsupportedOperationException("not implemented");
    }
}
