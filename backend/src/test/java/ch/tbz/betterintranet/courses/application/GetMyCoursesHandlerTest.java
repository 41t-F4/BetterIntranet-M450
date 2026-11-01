package ch.tbz.betterintranet.courses.application;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.when;

import ch.tbz.betterintranet.core.error.NotFoundException;
import ch.tbz.betterintranet.core.error.UpstreamUnavailableException;
import ch.tbz.betterintranet.courses.api.Course;
import ch.tbz.betterintranet.courses.api.GetMyCoursesQuery;
import java.util.List;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class GetMyCoursesHandlerTest {

    private static final Course ENG_HS26 = course("ENG-HS26", "ENG", "HS 2026");
    private static final Course GES_HS26 = course("GES-HS26", "GES", "HS 2026");
    private static final Course M450_HS26 = course("450-HS26", "450", "HS 2026");
    private static final Course M306_HS26 = course("306-HS26", "306", "HS 2026");
    private static final Course ENG_FS26 = course("ENG-FS26", "ENG", "FS 2026");
    private static final Course M320_FS26 = course("320-FS26", "320", "FS 2026");
    private static final Course M322_FS26 = course("322-FS26", "322", "FS 2026");
    private static final Course M117_HS25 = course("117-HS25", "117", "HS 2025");
    private static final Course M231_FS27 = course("231-FS27", "231", "FS 2027");
    private static final Course UNPARSEABLE_A = course("X-A", "A", "Sommerkurs");
    private static final Course UNPARSEABLE_B = course("X-B", "B", null);

    @Mock
    private CoursesClient client;

    @InjectMocks
    private GetMyCoursesHandler handler;

    @Test
    @DisplayName("asks the client for the courses of the queried user")
    void loadsCoursesOfQueriedUser() {
        when(client.coursesOf("max.muster")).thenReturn(List.of(M450_HS26));

        assertThat(handler.handle(new GetMyCoursesQuery("max.muster"))).containsExactly(M450_HS26);
    }

    @Test
    @DisplayName("sorts the newest semester first and then by code")
    void sortsNewestSemesterFirstThenCode() {
        when(client.coursesOf("nicola.wehrli")).thenReturn(
                List.of(ENG_FS26, M450_HS26, M322_FS26, GES_HS26, M320_FS26, ENG_HS26, M306_HS26));

        List<Course> result = handler.handle(new GetMyCoursesQuery("nicola.wehrli"));

        assertThat(result).containsExactly(
                M306_HS26, M450_HS26, ENG_HS26, GES_HS26, M320_FS26, M322_FS26, ENG_FS26);
    }

    @Test
    @DisplayName("a higher year comes first and within the same year HS comes before FS")
    void ordersSemestersByYearThenHalf() {
        when(client.coursesOf("nicola.wehrli")).thenReturn(List.of(M117_HS25, ENG_FS26, M231_FS27, M450_HS26));

        assertThat(handler.handle(new GetMyCoursesQuery("nicola.wehrli")))
                .containsExactly(M231_FS27, M450_HS26, ENG_FS26, M117_HS25);
    }

    @Test
    @DisplayName("courses with an unparseable or missing semester are sorted last and then by code")
    void unparseableSemesterSortsLast() {
        when(client.coursesOf("nicola.wehrli")).thenReturn(
                List.of(UNPARSEABLE_B, M117_HS25, UNPARSEABLE_A, ENG_FS26));

        assertThat(handler.handle(new GetMyCoursesQuery("nicola.wehrli")))
                .containsExactly(ENG_FS26, M117_HS25, UNPARSEABLE_A, UNPARSEABLE_B);
    }

    @Test
    @DisplayName("filters by semester when one is given, keeping the sort order")
    void filtersBySemester() {
        when(client.coursesOf("nicola.wehrli")).thenReturn(
                List.of(ENG_FS26, M450_HS26, M322_FS26, GES_HS26, M320_FS26, ENG_HS26, M306_HS26));

        List<Course> result = handler.handle(new GetMyCoursesQuery("nicola.wehrli", "HS 2026"));

        assertThat(result).containsExactly(M306_HS26, M450_HS26, ENG_HS26, GES_HS26);
    }

    @Test
    @DisplayName("returns an empty list when no course matches the semester")
    void returnsEmptyListWhenNoCourseMatchesSemester() {
        when(client.coursesOf("nicola.wehrli")).thenReturn(List.of(M450_HS26, ENG_FS26));

        assertThat(handler.handle(new GetMyCoursesQuery("nicola.wehrli", "FS 2027"))).isEmpty();
    }

    @Test
    @DisplayName("passes NotFoundException of an unknown user through")
    void passesNotFoundThrough() {
        when(client.coursesOf("unknown")).thenThrow(new NotFoundException("Unknown user 'unknown'"));

        assertThatThrownBy(() -> handler.handle(new GetMyCoursesQuery("unknown")))
                .isInstanceOf(NotFoundException.class)
                .hasMessageContaining("unknown");
    }

    @Test
    @DisplayName("passes UpstreamUnavailableException through")
    void passesUpstreamUnavailableThrough() {
        when(client.coursesOf("nicola.wehrli")).thenThrow(new UpstreamUnavailableException("school", "down"));

        assertThatThrownBy(() -> handler.handle(new GetMyCoursesQuery("nicola.wehrli")))
                .isInstanceOf(UpstreamUnavailableException.class);
    }

    private static Course course(String id, String code, String semester) {
        return new Course(id, code, "Course " + code, "VAJ", semester, 4, "AU-060");
    }
}
