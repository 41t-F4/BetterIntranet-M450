package ch.tbz.betterintranet.courses.web;

import ch.tbz.betterintranet.core.gateway.QueryGateway;
import ch.tbz.betterintranet.core.web.CurrentUser;
import ch.tbz.betterintranet.courses.api.Course;
import java.util.List;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/** {@code GET /api/courses[?semester=HS 2026]}: the courses of the current user. */
@RestController
@RequestMapping("/api/courses")
public class CoursesController {

    private final QueryGateway queries;

    public CoursesController(QueryGateway queries) {
        this.queries = queries;
    }

    @GetMapping
    public List<Course> myCourses(CurrentUser user, @RequestParam(required = false) String semester) {
        throw new UnsupportedOperationException("not implemented");
    }
}
