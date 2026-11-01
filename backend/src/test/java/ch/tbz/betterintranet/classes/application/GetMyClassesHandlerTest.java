package ch.tbz.betterintranet.classes.application;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.when;

import ch.tbz.betterintranet.classes.api.GetMyClassesQuery;
import ch.tbz.betterintranet.classes.api.SchoolClassMembership;
import ch.tbz.betterintranet.core.error.NotFoundException;
import ch.tbz.betterintranet.core.error.UpstreamUnavailableException;
import java.util.List;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class GetMyClassesHandlerTest {

    private static final SchoolClassMembership AP24B_2026 = membership(7471, "AP 24b", "2026/27");
    private static final SchoolClassMembership AP24B_2025 = membership(6890, "AP 24b", "2025/26");
    private static final SchoolClassMembership PE26A_2026 = membership(7372, "PE 26a", "2026/27");
    private static final SchoolClassMembership AP23A_2024 = membership(5001, "AP 23a", "2024/25");

    @Mock
    private ClassesClient client;

    @InjectMocks
    private GetMyClassesHandler handler;

    @Test
    @DisplayName("asks the client for the classes of the queried user")
    void loadsClassesOfQueriedUser() {
        when(client.classesOf("max.muster")).thenReturn(List.of(AP24B_2026));

        List<SchoolClassMembership> result = handler.handle(new GetMyClassesQuery("max.muster"));

        assertThat(result).containsExactly(AP24B_2026);
    }

    @Test
    @DisplayName("sorts by school year descending, then by short name")
    void sortsBySchoolYearDescThenShortName() {
        when(client.classesOf("nicola.wehrli")).thenReturn(List.of(AP23A_2024, PE26A_2026, AP24B_2025, AP24B_2026));

        List<SchoolClassMembership> result = handler.handle(new GetMyClassesQuery("nicola.wehrli"));

        assertThat(result).containsExactly(AP24B_2026, PE26A_2026, AP24B_2025, AP23A_2024);
    }

    @Test
    @DisplayName("returns an empty list when the user has no classes")
    void returnsEmptyListWithoutClasses() {
        when(client.classesOf("nicola.wehrli")).thenReturn(List.of());

        assertThat(handler.handle(new GetMyClassesQuery("nicola.wehrli"))).isEmpty();
    }

    @Test
    @DisplayName("passes NotFoundException of an unknown user through")
    void passesNotFoundThrough() {
        when(client.classesOf("unknown")).thenThrow(new NotFoundException("Unknown user 'unknown'"));

        assertThatThrownBy(() -> handler.handle(new GetMyClassesQuery("unknown")))
                .isInstanceOf(NotFoundException.class)
                .hasMessageContaining("unknown");
    }

    @Test
    @DisplayName("passes UpstreamUnavailableException through")
    void passesUpstreamUnavailableThrough() {
        when(client.classesOf("nicola.wehrli")).thenThrow(new UpstreamUnavailableException("school", "down"));

        assertThatThrownBy(() -> handler.handle(new GetMyClassesQuery("nicola.wehrli")))
                .isInstanceOf(UpstreamUnavailableException.class);
    }

    private static SchoolClassMembership membership(long id, String shortName, String schoolYear) {
        return new SchoolClassMembership(id, shortName, "Informatiker/innen Applikationsentwicklung", "IT", "MID",
                schoolYear, "STUDENT");
    }
}
