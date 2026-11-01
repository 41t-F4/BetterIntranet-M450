package ch.tbz.betterintranet.grades.application;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.assertj.core.groups.Tuple.tuple;
import static org.mockito.Mockito.when;

import ch.tbz.betterintranet.core.error.NotFoundException;
import ch.tbz.betterintranet.grades.api.GetGradesOverviewQuery;
import ch.tbz.betterintranet.grades.api.GradeEntry;
import ch.tbz.betterintranet.grades.api.GradesOverview;
import ch.tbz.betterintranet.grades.api.SubjectGrades;
import java.time.LocalDate;
import java.util.List;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class GetGradesOverviewHandlerTest {

    private static final String USER = "nicola.wehrli";
    private static final GetGradesOverviewQuery QUERY = new GetGradesOverviewQuery(USER);

    @Mock
    private GradesClient client;

    @InjectMocks
    private GetGradesOverviewHandler handler;

    @Test
    @DisplayName("a user without grades gets an empty overview without average")
    void shouldReturnEmptyOverviewWithoutGrades() {
        when(client.fetchGrades(USER)).thenReturn(List.of());

        GradesOverview overview = handler.handle(QUERY);

        assertThat(overview.overallAverage()).isNull();
        assertThat(overview.subjects()).isEmpty();
    }

    @Test
    @DisplayName("grades are grouped into one flat list entry per subject and semester")
    void shouldGroupBySubjectAndSemester() {
        when(client.fetchGrades(USER)).thenReturn(List.of(
                grade(1, "Englisch", "HS 2026", "Presentation", "2026-10-23", 5.5, 1.0),
                grade(2, "Englisch", "FS 2026", "Listening", "2026-05-21", 5.0, 1.0),
                grade(3, "Englisch", "HS 2026", "Vocabulary", "2026-09-18", 5.0, 1.0)));

        GradesOverview overview = handler.handle(QUERY);

        assertThat(overview.subjects())
                .extracting(SubjectGrades::subject, SubjectGrades::semester, subject -> subject.grades().size())
                .containsExactly(
                        tuple("Englisch", "HS 2026", 2),
                        tuple("Englisch", "FS 2026", 1));
    }

    @Test
    @DisplayName("subjects are sorted newest semester first (HS before FS of the same year), then by name")
    void shouldSortByNewestSemesterThenSubject() {
        when(client.fetchGrades(USER)).thenReturn(List.of(
                grade(1, "Englisch", "FS 2026", "Listening", "2026-05-21", 5.0, 1.0),
                grade(2, "Allgemeinbildung Gesellschaft", "HS 2025", "Politik", "2025-10-01", 4.5, 1.0),
                grade(3, "Englisch", "HS 2026", "Presentation", "2026-10-23", 5.5, 1.0),
                grade(4, "Sport", "Sondersemester", "Lauf", "2026-06-30", 6.0, 1.0),
                grade(5, "450 Applikationen testen", "HS 2026", "LB1", "2026-09-29", 5.5, 1.0),
                grade(6, "320 Objektorientiert programmieren", "FS 2026", "LB1", "2026-04-02", 6.0, 1.0),
                grade(7, "Allgemeinbildung Gesellschaft", "FS 2027", "Recht", "2027-03-15", 5.0, 1.0)));

        GradesOverview overview = handler.handle(QUERY);

        assertThat(overview.subjects()).extracting(SubjectGrades::semester, SubjectGrades::subject).containsExactly(
                tuple("FS 2027", "Allgemeinbildung Gesellschaft"),
                tuple("HS 2026", "450 Applikationen testen"),
                tuple("HS 2026", "Englisch"),
                tuple("FS 2026", "320 Objektorientiert programmieren"),
                tuple("FS 2026", "Englisch"),
                tuple("HS 2025", "Allgemeinbildung Gesellschaft"),
                tuple("Sondersemester", "Sport"));
    }

    @Test
    @DisplayName("grade entries of a subject are sorted by date, newest first, and keep all fields")
    void shouldSortEntriesByDateDescending() {
        when(client.fetchGrades(USER)).thenReturn(List.of(
                grade(1, "450 Applikationen testen", "HS 2026", "LB1 Testkonzept", "2026-09-29", 5.5, 1.0),
                grade(2, "450 Applikationen testen", "HS 2026", "LB2 Projektarbeit", "2026-10-27", 5.0, 2.0),
                grade(3, "450 Applikationen testen", "HS 2026", "LB0 Einstieg", "2026-08-20", 4.5, 0.5)));

        GradesOverview overview = handler.handle(QUERY);

        List<GradeEntry> entries = overview.subjects().getFirst().grades();
        assertThat(entries).containsExactly(
                new GradeEntry(2, "LB2 Projektarbeit", LocalDate.parse("2026-10-27"), 5.0, 2.0),
                new GradeEntry(1, "LB1 Testkonzept", LocalDate.parse("2026-09-29"), 5.5, 1.0),
                new GradeEntry(3, "LB0 Einstieg", LocalDate.parse("2026-08-20"), 4.5, 0.5));
    }

    @Test
    @DisplayName("a subject average is weighted and comes with its Swiss report grade")
    void shouldComputeWeightedSubjectAverageAndReportGrade() {
        when(client.fetchGrades(USER)).thenReturn(List.of(
                grade(1, "450 Applikationen testen", "HS 2026", "LB1", "2026-09-29", 5.5, 1.0),
                grade(2, "450 Applikationen testen", "HS 2026", "LB2", "2026-10-27", 5.0, 2.0)));

        GradesOverview overview = handler.handle(QUERY);

        SubjectGrades subject = overview.subjects().getFirst();
        assertThat(subject.average()).isEqualTo(5.17);
        assertThat(subject.reportGrade()).isEqualTo(5.0);
    }

    @Test
    @DisplayName("weight-0 entries are listed but do not influence the subject average")
    void shouldIgnoreZeroWeightEntriesInAverage() {
        when(client.fetchGrades(USER)).thenReturn(List.of(
                grade(1, "320 Objektorientiert programmieren", "FS 2026", "LB1", "2026-04-02", 6.0, 1.0),
                grade(2, "320 Objektorientiert programmieren", "FS 2026", "Übung", "2026-03-05", 4.0, 0.0)));

        GradesOverview overview = handler.handle(QUERY);

        SubjectGrades subject = overview.subjects().getFirst();
        assertThat(subject.average()).isEqualTo(6.0);
        assertThat(subject.grades()).hasSize(2);
    }

    @Test
    @DisplayName("the overall average is the plain mean of the rounded subject averages")
    void shouldComputeOverallAverageAsPlainMeanOfSubjectAverages() {
        when(client.fetchGrades(USER)).thenReturn(List.of(
                grade(1, "Englisch", "HS 2026", "Presentation", "2026-10-23", 5.0, 1.0),
                grade(2, "Englisch", "HS 2026", "Vocabulary", "2026-09-18", 4.5, 0.5),
                grade(3, "Allgemeinbildung Gesellschaft", "HS 2026", "Recht", "2026-09-24", 5.5, 1.0),
                grade(4, "Allgemeinbildung Gesellschaft", "HS 2026", "Politik", "2026-10-15", 4.0, 1.0),
                grade(5, "450 Applikationen testen", "FS 2026", "LB1", "2026-03-30", 5.5, 1.0),
                grade(6, "450 Applikationen testen", "FS 2026", "LB2", "2026-04-28", 5.0, 2.0)));

        GradesOverview overview = handler.handle(QUERY);

        assertThat(overview.subjects()).extracting(SubjectGrades::average).containsExactly(4.75, 4.83, 5.17);
        assertThat(overview.overallAverage()).isEqualTo(4.92);
    }

    @Test
    @DisplayName("a subject with only weight-0 entries has no average and is excluded from the overall average")
    void shouldExcludeSubjectsWithoutAverageFromOverallAverage() {
        when(client.fetchGrades(USER)).thenReturn(List.of(
                grade(1, "320 Objektorientiert programmieren", "HS 2026", "Übung", "2026-09-10", 4.0, 0.0),
                grade(2, "450 Applikationen testen", "HS 2026", "LB1", "2026-09-29", 5.5, 1.0),
                grade(3, "450 Applikationen testen", "HS 2026", "LB2", "2026-10-27", 5.0, 2.0)));

        GradesOverview overview = handler.handle(QUERY);

        SubjectGrades withoutAverage = overview.subjects().getFirst();
        assertThat(withoutAverage.subject()).isEqualTo("320 Objektorientiert programmieren");
        assertThat(withoutAverage.average()).isNull();
        assertThat(withoutAverage.reportGrade()).isNull();
        assertThat(overview.overallAverage()).isEqualTo(5.17);
    }

    @Test
    @DisplayName("only weight-0 entries give no overall average")
    void shouldHaveNoOverallAverageWhenNoSubjectHasOne() {
        when(client.fetchGrades(USER)).thenReturn(List.of(
                grade(1, "Englisch", "HS 2026", "Übung", "2026-09-10", 4.0, 0.0)));

        GradesOverview overview = handler.handle(QUERY);

        assertThat(overview.subjects()).hasSize(1);
        assertThat(overview.overallAverage()).isNull();
    }

    @Test
    @DisplayName("an unknown user reported by the client is passed through as NotFoundException")
    void shouldPassThroughNotFound() {
        when(client.fetchGrades(USER)).thenThrow(new NotFoundException("User nicola.wehrli not found"));

        assertThatThrownBy(() -> handler.handle(QUERY))
                .isInstanceOf(NotFoundException.class)
                .hasMessage("User nicola.wehrli not found");
    }

    private static RawGrade grade(long id, String subject, String semester, String title,
                                  String date, double grade, double weight) {
        return new RawGrade(id, subject, semester, title, LocalDate.parse(date), grade, weight);
    }
}
