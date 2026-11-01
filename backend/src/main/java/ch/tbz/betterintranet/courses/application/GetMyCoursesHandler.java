package ch.tbz.betterintranet.courses.application;

import ch.tbz.betterintranet.core.gateway.QueryHandler;
import ch.tbz.betterintranet.courses.api.Course;
import ch.tbz.betterintranet.courses.api.GetMyCoursesQuery;
import java.util.Comparator;
import java.util.List;
import org.springframework.stereotype.Component;

/**
 * Loads the user's courses from the school system, filters by semester and sorts by school part in the order
 * {@code BMS}, {@code TBZ}, {@code UEK} (unknown parts last) and then by code.
 */
@Component
public class GetMyCoursesHandler implements QueryHandler<GetMyCoursesQuery, List<Course>> {

    private static final List<String> SCHOOL_PART_ORDER = List.of("BMS", "TBZ", "UEK");

    private static final Comparator<Course> SCHOOL_PART_THEN_CODE =
            Comparator.comparingInt(GetMyCoursesHandler::schoolPartRank)
                    .thenComparing(Course::code);

    private final CoursesClient client;

    public GetMyCoursesHandler(CoursesClient client) {
        this.client = client;
    }

    @Override
    public List<Course> handle(GetMyCoursesQuery query) {
        return client.coursesOf(query.username()).stream()
                .filter(course -> !query.hasSemester() || query.semester().equals(course.semester()))
                .sorted(SCHOOL_PART_THEN_CODE)
                .toList();
    }

    private static int schoolPartRank(Course course) {
        int index = SCHOOL_PART_ORDER.indexOf(course.schoolPart());
        return index < 0 ? SCHOOL_PART_ORDER.size() : index;
    }
}
