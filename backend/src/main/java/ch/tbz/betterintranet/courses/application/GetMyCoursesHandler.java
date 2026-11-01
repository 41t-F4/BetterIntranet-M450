package ch.tbz.betterintranet.courses.application;

import ch.tbz.betterintranet.core.gateway.QueryHandler;
import ch.tbz.betterintranet.courses.api.Course;
import ch.tbz.betterintranet.courses.api.GetMyCoursesQuery;
import java.util.List;
import org.springframework.stereotype.Component;

/** Loads the user's courses from the school system, filters by semester and sorts by school part and code. */
@Component
public class GetMyCoursesHandler implements QueryHandler<GetMyCoursesQuery, List<Course>> {

    private final CoursesClient client;

    public GetMyCoursesHandler(CoursesClient client) {
        this.client = client;
    }

    @Override
    public List<Course> handle(GetMyCoursesQuery query) {
        throw new UnsupportedOperationException("not implemented");
    }
}
