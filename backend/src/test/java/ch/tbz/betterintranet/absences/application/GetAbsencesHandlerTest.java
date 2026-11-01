package ch.tbz.betterintranet.absences.application;

import static ch.tbz.betterintranet.absences.api.AbsenceStatus.EXCUSED;
import static ch.tbz.betterintranet.absences.api.AbsenceStatus.OPEN;
import static ch.tbz.betterintranet.absences.api.AbsenceStatus.UNEXCUSED;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import ch.tbz.betterintranet.absences.api.Absence;
import ch.tbz.betterintranet.absences.api.AbsenceStatus;
import ch.tbz.betterintranet.absences.api.AbsenceSummary;
import ch.tbz.betterintranet.absences.api.AbsencesOverview;
import ch.tbz.betterintranet.absences.api.GetAbsencesQuery;
import ch.tbz.betterintranet.core.error.NotFoundException;
import ch.tbz.betterintranet.core.error.UpstreamUnavailableException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class GetAbsencesHandlerTest {

    private static final String USER = "nicola.wehrli";

    @Mock
    private AbsencesClient client;

    @InjectMocks
    private GetAbsencesHandler handler;

    @Test
    @DisplayName("absences are sorted by date descending, then by lesson start descending")
    void sortsNewestFirst() {
        when(client.fetchAbsences(USER)).thenReturn(List.of(
                absence(1, LocalDate.of(2026, 9, 14), LocalTime.of(8, 10), OPEN),
                absence(2, LocalDate.of(2026, 10, 12), LocalTime.of(8, 10), OPEN),
                absence(3, LocalDate.of(2026, 10, 12), LocalTime.of(10, 5), EXCUSED),
                absence(4, LocalDate.of(2026, 8, 31), LocalTime.of(13, 15), UNEXCUSED)));

        AbsencesOverview overview = handler.handle(new GetAbsencesQuery(USER));

        assertThat(overview.absences()).extracting(Absence::id).containsExactly(3L, 2L, 1L, 4L);
    }

    @Test
    @DisplayName("the summary counts every entry and the missed lessons")
    void computesSummary() {
        when(client.fetchAbsences(USER)).thenReturn(List.of(
                absence(1, LocalDate.of(2026, 10, 12), LocalTime.of(8, 10), LocalTime.of(9, 45), OPEN),
                absence(2, LocalDate.of(2026, 10, 12), LocalTime.of(10, 5), LocalTime.of(10, 50), EXCUSED),
                absence(3, LocalDate.of(2026, 9, 14), LocalTime.of(8, 10), LocalTime.of(8, 55), UNEXCUSED),
                absence(4, LocalDate.of(2026, 9, 7), LocalTime.of(8, 10), LocalTime.of(8, 55), EXCUSED)));

        AbsencesOverview overview = handler.handle(new GetAbsencesQuery(USER));

        assertThat(overview.summary()).isEqualTo(new AbsenceSummary(4, 1, 2, 1, 5));
    }

    @Test
    @DisplayName("a status filter keeps only matching entries but the summary still covers all of them")
    void filtersByStatus() {
        when(client.fetchAbsences(USER)).thenReturn(List.of(
                absence(1, LocalDate.of(2026, 10, 12), LocalTime.of(8, 10), OPEN),
                absence(2, LocalDate.of(2026, 9, 28), LocalTime.of(13, 15), EXCUSED),
                absence(3, LocalDate.of(2026, 9, 14), LocalTime.of(8, 10), UNEXCUSED),
                absence(4, LocalDate.of(2026, 8, 24), LocalTime.of(8, 10), EXCUSED)));

        AbsencesOverview overview = handler.handle(new GetAbsencesQuery(USER, EXCUSED));

        assertThat(overview.absences()).extracting(Absence::id).containsExactly(2L, 4L);
        assertThat(overview.absences()).extracting(Absence::status).containsOnly(EXCUSED);
        assertThat(overview.summary()).isEqualTo(new AbsenceSummary(4, 1, 2, 1, 4));
    }

    @Test
    @DisplayName("a user without absences gets an empty list and a zero summary")
    void handlesNoAbsences() {
        when(client.fetchAbsences(USER)).thenReturn(List.of());

        AbsencesOverview overview = handler.handle(new GetAbsencesQuery(USER));

        assertThat(overview.absences()).isEmpty();
        assertThat(overview.summary()).isEqualTo(new AbsenceSummary(0, 0, 0, 0, 0));
    }

    @Test
    @DisplayName("the username of the query is forwarded to the client")
    void forwardsUsername() {
        when(client.fetchAbsences("max.muster")).thenReturn(List.of());

        handler.handle(new GetAbsencesQuery("max.muster"));

        verify(client).fetchAbsences("max.muster");
    }

    @Test
    @DisplayName("an unknown user reported by the client is passed through as NotFoundException")
    void passesThroughNotFound() {
        when(client.fetchAbsences(USER)).thenThrow(new NotFoundException("User 'nicola.wehrli' not found"));

        assertThatThrownBy(() -> handler.handle(new GetAbsencesQuery(USER)))
                .isInstanceOf(NotFoundException.class)
                .hasMessageContaining("nicola.wehrli");
    }

    @Test
    @DisplayName("an unreachable school system is passed through as UpstreamUnavailableException")
    void passesThroughUpstreamFailure() {
        when(client.fetchAbsences(USER)).thenThrow(new UpstreamUnavailableException("school", "connection refused"));

        assertThatThrownBy(() -> handler.handle(new GetAbsencesQuery(USER)))
                .isInstanceOf(UpstreamUnavailableException.class);
    }

    private static Absence absence(long id, LocalDate date, LocalTime start, AbsenceStatus status) {
        return absence(id, date, start, start.plusMinutes(45), status);
    }

    private static Absence absence(long id, LocalDate date, LocalTime start, LocalTime end, AbsenceStatus status) {
        return new Absence(id, date, start, end, "Englisch", "MID", status, null, date.atTime(10, 0));
    }
}
