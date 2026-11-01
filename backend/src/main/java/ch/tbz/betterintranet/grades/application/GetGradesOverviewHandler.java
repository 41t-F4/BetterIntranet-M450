package ch.tbz.betterintranet.grades.application;

import static java.util.Comparator.comparing;
import static java.util.Comparator.reverseOrder;
import static java.util.stream.Collectors.groupingBy;
import static java.util.stream.Collectors.toList;

import ch.tbz.betterintranet.core.gateway.QueryHandler;
import ch.tbz.betterintranet.grades.api.GetGradesOverviewQuery;
import ch.tbz.betterintranet.grades.api.GradeEntry;
import ch.tbz.betterintranet.grades.api.GradesOverview;
import ch.tbz.betterintranet.grades.api.SchoolPart;
import ch.tbz.betterintranet.grades.api.SchoolPartGrades;
import ch.tbz.betterintranet.grades.api.SubjectGrades;
import ch.tbz.betterintranet.grades.domain.GradeAverageCalculator;
import ch.tbz.betterintranet.grades.domain.WeightedGrade;
import java.util.Comparator;
import java.util.EnumMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.OptionalDouble;
import java.util.stream.Stream;
import org.springframework.stereotype.Component;

/**
 * Loads the raw grades of a user and turns them into a grouped overview with averages.
 *
 * <p>Grouping and ordering: school parts in the order {@link SchoolPart#BMS}, {@link SchoolPart#TBZ},
 * {@link SchoolPart#UEK} (only those with grades); within a school part one {@link SubjectGrades} per
 * subject and semester, sorted by subject name and then newest semester first (the semester whose latest
 * entry is most recent); entries within a subject newest first.
 *
 * <p>Averages: a subject average is the weighted average of its entries, a school part average the plain
 * mean of its (already rounded) subject averages and the overall average the plain mean of the school part
 * averages. Subjects or school parts without an average are left out of the next level's mean.
 */
@Component
public class GetGradesOverviewHandler implements QueryHandler<GetGradesOverviewQuery, GradesOverview> {

    private static final Comparator<RawGrade> NEWEST_ENTRY_FIRST =
            comparing(RawGrade::date, reverseOrder()).thenComparing(RawGrade::id, reverseOrder());

    private static final Comparator<SubjectGrades> BY_SUBJECT_THEN_NEWEST_SEMESTER =
            comparing(SubjectGrades::subject)
                    .thenComparing(subject -> subject.grades().getFirst().date(), reverseOrder())
                    .thenComparing(SubjectGrades::semester, reverseOrder());

    private final GradesClient client;

    public GetGradesOverviewHandler(GradesClient client) {
        this.client = client;
    }

    @Override
    public GradesOverview handle(GetGradesOverviewQuery query) {
        List<RawGrade> rawGrades = client.fetchGrades(query.username());

        Map<SchoolPart, List<RawGrade>> bySchoolPart = rawGrades.stream()
                .collect(groupingBy(RawGrade::schoolPart, () -> new EnumMap<>(SchoolPart.class), toList()));
        List<SchoolPartGrades> schoolParts = bySchoolPart.entrySet().stream()
                .map(entry -> schoolPartGrades(entry.getKey(), entry.getValue()))
                .toList();

        return new GradesOverview(meanOf(schoolParts.stream().map(SchoolPartGrades::average)), schoolParts);
    }

    private static SchoolPartGrades schoolPartGrades(SchoolPart schoolPart, List<RawGrade> rawGrades) {
        List<SubjectGrades> subjects = rawGrades.stream()
                .collect(groupingBy(SubjectKey::of))
                .entrySet().stream()
                .map(entry -> subjectGrades(entry.getKey(), entry.getValue()))
                .sorted(BY_SUBJECT_THEN_NEWEST_SEMESTER)
                .toList();
        return new SchoolPartGrades(schoolPart, meanOf(subjects.stream().map(SubjectGrades::average)), subjects);
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

    /** Plain mean of the non-null averages, {@code null} when there is none. */
    private static Double meanOf(Stream<Double> averages) {
        return nullable(GradeAverageCalculator.mean(averages.filter(Objects::nonNull).toList()));
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
