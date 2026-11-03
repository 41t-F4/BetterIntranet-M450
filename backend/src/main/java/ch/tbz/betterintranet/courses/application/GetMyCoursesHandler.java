package ch.tbz.betterintranet.courses.application;

import ch.tbz.betterintranet.core.gateway.QueryHandler;
import ch.tbz.betterintranet.courses.api.Course;
import ch.tbz.betterintranet.courses.api.GetMyCoursesQuery;
import java.util.Comparator;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.springframework.stereotype.Component;

/**
 * Loads the user's courses from the school system, filters by semester and sorts the newest semester first
 * (higher year first, within a year {@code HS} before {@code FS}, unparseable semesters last) and then by code.
 */
@Component
public class GetMyCoursesHandler implements QueryHandler<GetMyCoursesQuery, List<Course>> {

    private static final Pattern SEMESTER = Pattern.compile("(HS|FS) (\\d{4})");

    private static final Comparator<Course> NEWEST_SEMESTER_THEN_CODE =
            Comparator.comparingInt(GetMyCoursesHandler::semesterRank).reversed()
                    .thenComparing(Course::code);

    private final CoursesClient client;

    public GetMyCoursesHandler(CoursesClient client) {
        this.client = client;
    }

    @Override
    public List<Course> handle(GetMyCoursesQuery query) {
        return client.coursesOf(query.username()).stream()
                .filter(course -> !query.hasSemester() || query.semester().equals(course.semester()))
                .sorted(NEWEST_SEMESTER_THEN_CODE)
                .toList();
    }

    /** Chronological rank of the semester: {@code FS y} &lt; {@code HS y} &lt; {@code FS y+1}; unparseable is lowest. */
    private static int semesterRank(Course course) {
        if (course.semester() == null) {
            return Integer.MIN_VALUE;
        }
        Matcher matcher = SEMESTER.matcher(course.semester().trim());
        if (!matcher.matches()) {
            return Integer.MIN_VALUE;
        }
        int year = Integer.parseInt(matcher.group(2));
        return year * 2 + ("HS".equals(matcher.group(1)) ? 1 : 0);
    }
}
