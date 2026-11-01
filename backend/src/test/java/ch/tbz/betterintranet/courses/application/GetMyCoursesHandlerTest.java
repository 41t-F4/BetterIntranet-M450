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

    private static final Course M450 = course("M450", "M450", "TBZ", "HS 2026");
    private static final Course M320 = course("M320", "M320", "TBZ", "HS 2026");
    private static final Course M322 = course("M322", "M322", "TBZ", "FS 2026");
    private static final Course BM_D = course("BM-D", "D", "BMS", "HS 2026");
    private static final Course BM_E = course("BM-E", "E", "BMS", "FS 2026");
    private static final Course UK335 = course("UK335", "ÜK 335", "UEK", "HS 2026");
    private static final Course OTHER = course("X1", "A", "OTHER", "HS 2026");

    @Mock
    private CoursesClient client;

    @InjectMocks
    private GetMyCoursesHandler handler;

    @Test
    @DisplayName("asks the client for the courses of the queried user")
    void loadsCoursesOfQueriedUser() {
        when(client.coursesOf("max.muster")).thenReturn(List.of(M450));

        assertThat(handler.handle(new GetMyCoursesQuery("max.muster"))).containsExactly(M450);
    }

    @Test
    @DisplayName("sorts by school part in the order BMS, TBZ, UEK and then by code")
    void sortsBySchoolPartOrderThenCode() {
        when(client.coursesOf("nicola.wehrli")).thenReturn(List.of(UK335, M450, BM_E, M322, BM_D, M320));

        List<Course> result = handler.handle(new GetMyCoursesQuery("nicola.wehrli"));

        assertThat(result).containsExactly(BM_D, BM_E, M320, M322, M450, UK335);
    }

    @Test
    @DisplayName("an unknown school part is sorted after the known ones")
    void unknownSchoolPartSortsLast() {
        when(client.coursesOf("nicola.wehrli")).thenReturn(List.of(OTHER, UK335, BM_D));

        assertThat(handler.handle(new GetMyCoursesQuery("nicola.wehrli"))).containsExactly(BM_D, UK335, OTHER);
    }

    @Test
    @DisplayName("filters by semester when one is given, keeping the sort order")
    void filtersBySemester() {
        when(client.coursesOf("nicola.wehrli")).thenReturn(List.of(UK335, M450, BM_E, M322, BM_D, M320));

        List<Course> result = handler.handle(new GetMyCoursesQuery("nicola.wehrli", "HS 2026"));

        assertThat(result).containsExactly(BM_D, M320, M450, UK335);
    }

    @Test
    @DisplayName("returns an empty list when no course matches the semester")
    void returnsEmptyListWhenNoCourseMatchesSemester() {
        when(client.coursesOf("nicola.wehrli")).thenReturn(List.of(M450, BM_D));

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

    private static Course course(String id, String code, String schoolPart, String semester) {
        return new Course(id, code, "Course " + code, "STU", semester, 4, "AU-512", schoolPart);
    }
}
