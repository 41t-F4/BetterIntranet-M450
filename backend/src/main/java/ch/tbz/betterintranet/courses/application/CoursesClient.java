package ch.tbz.betterintranet.courses.application;

import ch.tbz.betterintranet.courses.api.Course;
import java.util.List;

/** Port to the school system that knows which courses a user attends. */
public interface CoursesClient {

    /**
     * @throws ch.tbz.betterintranet.core.error.NotFoundException            if the user is unknown
     * @throws ch.tbz.betterintranet.core.error.UpstreamUnavailableException if the school system cannot be reached
     */
    List<Course> coursesOf(String username);
}
