package ch.tbz.betterintranet.grades.application;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.assertj.core.groups.Tuple.tuple;
import static org.mockito.Mockito.when;

import ch.tbz.betterintranet.core.error.NotFoundException;
import ch.tbz.betterintranet.grades.api.GetGradesOverviewQuery;
import ch.tbz.betterintranet.grades.api.GradeEntry;
import ch.tbz.betterintranet.grades.api.GradesOverview;
import ch.tbz.betterintranet.grades.api.SchoolPart;
import ch.tbz.betterintranet.grades.api.SchoolPartGrades;
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
    @DisplayName("a user without grades gets an empty overview without averages")
    void shouldReturnEmptyOverviewWithoutGrades() {
        when(client.fetchGrades(USER)).thenReturn(List.of());

        GradesOverview overview = handler.handle(QUERY);

        assertThat(overview.overallAverage()).isNull();
        assertThat(overview.schoolParts()).isEmpty();
    }

    @Test
    @DisplayName("school parts are ordered BMS, TBZ, ÜK regardless of upstream order and carry their label")
    void shouldOrderSchoolPartsAndLabelThem() {
        when(client.fetchGrades(USER)).thenReturn(List.of(
                grade(1, SchoolPart.UEK, "ÜK 335", "FS 2026", "KN", "2026-05-20", 5.0, 1.0),
                grade(2, SchoolPart.TBZ, "Modul 450", "HS 2026", "LB1", "2026-09-30", 5.5, 1.0),
                grade(3, SchoolPart.BMS, "Deutsch", "HS 2026", "Aufsatz", "2026-09-15", 5.0, 1.0)));

        GradesOverview overview = handler.handle(QUERY);

        assertThat(overview.schoolParts()).extracting(SchoolPartGrades::schoolPart)
                .containsExactly(SchoolPart.BMS, SchoolPart.TBZ, SchoolPart.UEK);
        assertThat(overview.schoolParts()).extracting(SchoolPartGrades::label)
                .containsExactly("BMS", "TBZ", "ÜK");
    }

    @Test
    @DisplayName("school parts without grades are left out")
    void shouldOmitSchoolPartsWithoutGrades() {
        when(client.fetchGrades(USER)).thenReturn(List.of(
                grade(1, SchoolPart.TBZ, "Modul 450", "HS 2026", "LB1", "2026-09-30", 5.5, 1.0)));

        GradesOverview overview = handler.handle(QUERY);

        assertThat(overview.schoolParts()).extracting(SchoolPartGrades::schoolPart).containsExactly(SchoolPart.TBZ);
    }

    @Test
    @DisplayName("subjects are sorted alphabetically, the newest semester of a subject first")
    void shouldSortSubjectsAlphabeticallyAndNewestSemesterFirst() {
        when(client.fetchGrades(USER)).thenReturn(List.of(
                grade(1, SchoolPart.BMS, "Mathematik", "HS 2026", "Algebra", "2026-09-22", 5.5, 1.0),
                grade(2, SchoolPart.BMS, "Deutsch", "FS 2026", "Vortrag", "2026-03-10", 4.5, 1.0),
                grade(3, SchoolPart.BMS, "Deutsch", "HS 2026", "Aufsatz", "2026-09-15", 5.0, 1.0),
                grade(4, SchoolPart.BMS, "Englisch", "HS 2026", "Listening", "2026-10-01", 5.0, 1.0)));

        GradesOverview overview = handler.handle(QUERY);

        List<SubjectGrades> subjects = overview.schoolParts().getFirst().subjects();
        assertThat(subjects).extracting(SubjectGrades::subject, SubjectGrades::semester).containsExactly(
                tuple("Deutsch", "HS 2026"),
                tuple("Deutsch", "FS 2026"),
                tuple("Englisch", "HS 2026"),
                tuple("Mathematik", "HS 2026"));
    }

    @Test
    @DisplayName("grade entries of a subject are sorted by date, newest first, and keep all fields")
    void shouldSortEntriesByDateDescending() {
        when(client.fetchGrades(USER)).thenReturn(List.of(
                grade(1, SchoolPart.TBZ, "Modul 450", "HS 2026", "LB1 Testkonzept", "2026-09-30", 5.5, 1.0),
                grade(2, SchoolPart.TBZ, "Modul 450", "HS 2026", "LB2 Projektarbeit", "2026-10-28", 5.0, 2.0),
                grade(3, SchoolPart.TBZ, "Modul 450", "HS 2026", "LB0 Einstieg", "2026-08-20", 4.5, 0.5)));

        GradesOverview overview = handler.handle(QUERY);

        List<GradeEntry> entries = overview.schoolParts().getFirst().subjects().getFirst().grades();
        assertThat(entries).containsExactly(
                new GradeEntry(2, "LB2 Projektarbeit", LocalDate.parse("2026-10-28"), 5.0, 2.0),
                new GradeEntry(1, "LB1 Testkonzept", LocalDate.parse("2026-09-30"), 5.5, 1.0),
                new GradeEntry(3, "LB0 Einstieg", LocalDate.parse("2026-08-20"), 4.5, 0.5));
    }

    @Test
    @DisplayName("a subject average is weighted and comes with its Swiss report grade")
    void shouldComputeWeightedSubjectAverageAndReportGrade() {
        when(client.fetchGrades(USER)).thenReturn(List.of(
                grade(1, SchoolPart.TBZ, "Modul 450", "HS 2026", "LB1", "2026-09-30", 5.5, 1.0),
                grade(2, SchoolPart.TBZ, "Modul 450", "HS 2026", "LB2", "2026-10-28", 5.0, 2.0)));

        GradesOverview overview = handler.handle(QUERY);

        SubjectGrades subject = overview.schoolParts().getFirst().subjects().getFirst();
        assertThat(subject.average()).isEqualTo(5.17);
        assertThat(subject.reportGrade()).isEqualTo(5.0);
    }

    @Test
    @DisplayName("weight-0 entries are listed but do not influence the subject average")
    void shouldIgnoreZeroWeightEntriesInAverage() {
        when(client.fetchGrades(USER)).thenReturn(List.of(
                grade(1, SchoolPart.TBZ, "Modul 320", "HS 2026", "LB1", "2026-10-05", 6.0, 1.0),
                grade(2, SchoolPart.TBZ, "Modul 320", "HS 2026", "Übung", "2026-09-10", 4.0, 0.0)));

        GradesOverview overview = handler.handle(QUERY);

        SubjectGrades subject = overview.schoolParts().getFirst().subjects().getFirst();
        assertThat(subject.average()).isEqualTo(6.0);
        assertThat(subject.grades()).hasSize(2);
    }

    @Test
    @DisplayName("a subject with only weight-0 entries has no average and is excluded from the school part average")
    void shouldExcludeSubjectsWithoutAverageFromSchoolPartAverage() {
        when(client.fetchGrades(USER)).thenReturn(List.of(
                grade(1, SchoolPart.TBZ, "Modul 320", "HS 2026", "Übung", "2026-09-10", 4.0, 0.0),
                grade(2, SchoolPart.TBZ, "Modul 450", "HS 2026", "LB1", "2026-09-30", 5.5, 1.0),
                grade(3, SchoolPart.TBZ, "Modul 450", "HS 2026", "LB2", "2026-10-28", 5.0, 2.0)));

        GradesOverview overview = handler.handle(QUERY);

        SchoolPartGrades tbz = overview.schoolParts().getFirst();
        assertThat(tbz.subjects()).extracting(SubjectGrades::subject).containsExactly("Modul 320", "Modul 450");
        assertThat(tbz.subjects().getFirst().average()).isNull();
        assertThat(tbz.subjects().getFirst().reportGrade()).isNull();
        assertThat(tbz.average()).isEqualTo(5.17);
    }

    @Test
    @DisplayName("school part and overall averages are plain means of the rounded averages below them")
    void shouldComputeSchoolPartAndOverallAveragesAsPlainMeans() {
        when(client.fetchGrades(USER)).thenReturn(List.of(
                grade(1, SchoolPart.BMS, "Deutsch", "HS 2026", "Aufsatz", "2026-09-15", 5.0, 1.0),
                grade(2, SchoolPart.BMS, "Deutsch", "HS 2026", "Grammatik", "2026-10-20", 4.5, 0.5),
                grade(3, SchoolPart.BMS, "Mathematik", "HS 2026", "Algebra", "2026-09-22", 5.5, 1.0),
                grade(4, SchoolPart.BMS, "Mathematik", "HS 2026", "Funktionen", "2026-10-27", 4.0, 1.0),
                grade(5, SchoolPart.TBZ, "Modul 450", "HS 2026", "LB1", "2026-09-30", 5.5, 1.0),
                grade(6, SchoolPart.TBZ, "Modul 450", "HS 2026", "LB2", "2026-10-28", 5.0, 2.0)));

        GradesOverview overview = handler.handle(QUERY);

        SchoolPartGrades bms = overview.schoolParts().get(0);
        SchoolPartGrades tbz = overview.schoolParts().get(1);
        assertThat(bms.subjects()).extracting(SubjectGrades::average).containsExactly(4.83, 4.75);
        assertThat(bms.average()).isEqualTo(4.79);
        assertThat(tbz.average()).isEqualTo(5.17);
        assertThat(overview.overallAverage()).isEqualTo(4.98);
    }

    @Test
    @DisplayName("a school part without any subject average has no average and is excluded from the overall average")
    void shouldExcludeSchoolPartsWithoutAverageFromOverall() {
        when(client.fetchGrades(USER)).thenReturn(List.of(
                grade(1, SchoolPart.UEK, "ÜK 335", "FS 2026", "Übung", "2026-05-20", 5.0, 0.0),
                grade(2, SchoolPart.TBZ, "Modul 450", "HS 2026", "LB1", "2026-09-30", 5.5, 1.0)));

        GradesOverview overview = handler.handle(QUERY);

        assertThat(overview.schoolParts().get(1).average()).isNull();
        assertThat(overview.overallAverage()).isEqualTo(5.5);
    }

    @Test
    @DisplayName("an unknown user reported by the client is passed through as NotFoundException")
    void shouldPassThroughNotFound() {
        when(client.fetchGrades(USER)).thenThrow(new NotFoundException("User nicola.wehrli not found"));

        assertThatThrownBy(() -> handler.handle(QUERY))
                .isInstanceOf(NotFoundException.class)
                .hasMessage("User nicola.wehrli not found");
    }

    private static RawGrade grade(long id, SchoolPart part, String subject, String semester, String title,
                                  String date, double grade, double weight) {
        return new RawGrade(id, part, subject, semester, title, LocalDate.parse(date), grade, weight);
    }
}
