package ch.tbz.betterintranet.timetable.application;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.verify;

import ch.tbz.betterintranet.timetable.api.GetClassesQuery;
import ch.tbz.betterintranet.timetable.api.SchoolClass;
import java.util.List;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class GetClassesHandlerTest {

    private static final SchoolClass AP24A = new SchoolClass(7468, "AP 24a", "Applikationsentwicklung", "IT");
    private static final SchoolClass AP24B = new SchoolClass(7471, "AP 24b", "Applikationsentwicklung", "IT");
    private static final SchoolClass PE26A = new SchoolClass(7372, "PE 26a", "Plattformentwicklung", "IT");

    @Mock
    private TimetableClient client;

    @InjectMocks
    private GetClassesHandler handler;

    @Test
    @DisplayName("classes come from the client sorted by short name")
    void sortsClassesByShortName() {
        given(client.fetchClasses()).willReturn(List.of(PE26A, AP24B, AP24A));

        List<SchoolClass> classes = handler.handle(new GetClassesQuery());

        assertThat(classes).containsExactly(AP24A, AP24B, PE26A);
        verify(client).fetchClasses();
    }

    @Test
    @DisplayName("no classes upstream gives an empty list")
    void emptyUpstream() {
        given(client.fetchClasses()).willReturn(List.of());

        assertThat(handler.handle(new GetClassesQuery())).isEmpty();
    }
}
