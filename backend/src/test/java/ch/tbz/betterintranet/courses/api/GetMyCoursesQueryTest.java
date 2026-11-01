package ch.tbz.betterintranet.courses.api;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.NullAndEmptySource;
import org.junit.jupiter.params.provider.ValueSource;

class GetMyCoursesQueryTest {

    @Test
    @DisplayName("without a semester the query covers all semesters")
    void withoutSemesterCoversAllSemesters() {
        GetMyCoursesQuery query = new GetMyCoursesQuery("nicola.wehrli");

        assertThat(query.username()).isEqualTo("nicola.wehrli");
        assertThat(query.semester()).isNull();
        assertThat(query.hasSemester()).isFalse();
    }

    @Test
    @DisplayName("a semester filter is kept trimmed")
    void keepsSemesterTrimmed() {
        GetMyCoursesQuery query = new GetMyCoursesQuery("nicola.wehrli", " HS 2026 ");

        assertThat(query.semester()).isEqualTo("HS 2026");
        assertThat(query.hasSemester()).isTrue();
    }

    @ParameterizedTest
    @NullAndEmptySource
    @ValueSource(strings = {"  "})
    @DisplayName("a blank semester means no filter")
    void blankSemesterMeansNoFilter(String semester) {
        GetMyCoursesQuery query = new GetMyCoursesQuery("nicola.wehrli", semester);

        assertThat(query.semester()).isNull();
        assertThat(query.hasSemester()).isFalse();
    }

    @ParameterizedTest
    @NullAndEmptySource
    @ValueSource(strings = {"   "})
    @DisplayName("rejects a missing username")
    void rejectsBlankUsername(String username) {
        assertThatThrownBy(() -> new GetMyCoursesQuery(username))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("username");
    }
}
