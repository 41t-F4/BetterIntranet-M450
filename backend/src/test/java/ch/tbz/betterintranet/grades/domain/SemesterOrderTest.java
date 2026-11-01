package ch.tbz.betterintranet.grades.domain;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

class SemesterOrderTest {

    private final Comparator<String> order = SemesterOrder.newestFirst();

    @ParameterizedTest(name = "{0} comes before {1}")
    @CsvSource({
            "HS 2026, FS 2026",
            "FS 2027, HS 2026",
            "HS 2026, HS 2025",
            "FS 2026, FS 2025",
            "FS 2026, HS 2025",
            "FS 2020, Sondersemester",
            "Blockwoche, Sondersemester"
    })
    void shouldPutNewerSemesterFirst(String newer, String older) {
        assertThat(order.compare(newer, older)).isNegative();
        assertThat(order.compare(older, newer)).isPositive();
    }

    @Test
    @DisplayName("equal labels are equal")
    void shouldTreatEqualLabelsAsEqual() {
        assertThat(order.compare("HS 2026", "HS 2026")).isZero();
        assertThat(order.compare("Sondersemester", "Sondersemester")).isZero();
    }

    @Test
    @DisplayName("a mixed list is sorted newest first, unparseable labels last and alphabetically")
    void shouldSortMixedList() {
        List<String> semesters = new ArrayList<>(Arrays.asList(
                "FS 2025", "Zusatzkurs", "HS 2026", "FS 2026", "Blockwoche", "HS 2025", "FS 2027"));

        semesters.sort(order);

        assertThat(semesters).containsExactly(
                "FS 2027", "HS 2026", "FS 2026", "HS 2025", "FS 2025", "Blockwoche", "Zusatzkurs");
    }

    @Test
    @DisplayName("a missing semester counts as unparseable and goes after all labels")
    void shouldPutNullLast() {
        assertThat(order.compare("HS 2026", null)).isNegative();
        assertThat(order.compare("Blockwoche", null)).isNegative();
        assertThat(order.compare(null, "FS 2020")).isPositive();
        assertThat(order.compare(null, null)).isZero();
    }
}
