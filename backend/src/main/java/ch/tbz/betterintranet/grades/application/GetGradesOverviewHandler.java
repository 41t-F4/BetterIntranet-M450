package ch.tbz.betterintranet.grades.application;

import static java.util.Comparator.comparing;
import static java.util.Comparator.reverseOrder;
import static java.util.stream.Collectors.groupingBy;

import ch.tbz.betterintranet.core.gateway.QueryHandler;
import ch.tbz.betterintranet.grades.api.GetGradesOverviewQuery;
import ch.tbz.betterintranet.grades.api.GradeEntry;
import ch.tbz.betterintranet.grades.api.GradesOverview;
import ch.tbz.betterintranet.grades.api.SubjectGrades;
import ch.tbz.betterintranet.grades.domain.GradeAverageCalculator;
import ch.tbz.betterintranet.grades.domain.SemesterOrder;
import ch.tbz.betterintranet.grades.domain.WeightedGrade;
import java.util.Comparator;
import java.util.List;
import java.util.Objects;
import java.util.OptionalDouble;
import org.springframework.stereotype.Component;

/**
 * Loads the raw grades of a user and turns them into a flat overview with averages.
 *
 * <p>Grouping and ordering: one {@link SubjectGrades} per subject and semester, sorted newest semester
 * first (see {@link SemesterOrder}) and then by subject name; entries within a subject newest first.
 *
 * <p>Averages: a subject average is the weighted average of its entries, the overall average the plain
 * mean of the (already rounded) subject averages. Subjects without an average are left out of that mean.
 */
@Component
public class GetGradesOverviewHandler implements QueryHandler<GetGradesOverviewQuery, GradesOverview> {

    private static final Comparator<RawGrade> NEWEST_ENTRY_FIRST =
            comparing(RawGrade::date, reverseOrder()).thenComparing(RawGrade::id, reverseOrder());

    private static final Comparator<SubjectGrades> NEWEST_SEMESTER_THEN_SUBJECT =
            comparing(SubjectGrades::semester, SemesterOrder.newestFirst())
                    .thenComparing(SubjectGrades::subject);

    private final GradesClient client;

    public GetGradesOverviewHandler(GradesClient client) {
        this.client = client;
    }

    @Override
    public GradesOverview handle(GetGradesOverviewQuery query) {
        List<SubjectGrades> subjects = client.fetchGrades(query.username()).stream()
                .collect(groupingBy(SubjectKey::of))
                .entrySet().stream()
                .map(entry -> subjectGrades(entry.getKey(), entry.getValue()))
                .sorted(NEWEST_SEMESTER_THEN_SUBJECT)
                .toList();
        List<Double> subjectAverages = subjects.stream().map(SubjectGrades::average).filter(Objects::nonNull).toList();
        return new GradesOverview(nullable(GradeAverageCalculator.mean(subjectAverages)), subjects);
    }

    private static SubjectGrades subjectGrades(SubjectKey key, List<RawGrade> rawGrades) {
        List<GradeEntry> entries = rawGrades.stream()
                .sorted(NEWEST_ENTRY_FIRST)
                .map(grade -> new GradeEntry(grade.id(), grade.title(), grade.date(), grade.grade(), grade.weight()))
                .toList();
        OptionalDouble average = GradeAverageCalculator.weightedAverage(rawGrades.stream()
                .map(grade -> new WeightedGrade(grade.grade(), grade.weight()))
                .toList());
        Double reportGrade = average.isPresent() ? GradeAverageCalculator.roundToSwissHalf(average.getAsDouble()) : null;
        return new SubjectGrades(key.subject(), key.semester(), nullable(average), reportGrade, entries);
    }

    private static Double nullable(OptionalDouble value) {
        return value.isPresent() ? value.getAsDouble() : null;
    }

    private record SubjectKey(String subject, String semester) {

        static SubjectKey of(RawGrade grade) {
            return new SubjectKey(grade.subject(), grade.semester());
        }
    }
}
