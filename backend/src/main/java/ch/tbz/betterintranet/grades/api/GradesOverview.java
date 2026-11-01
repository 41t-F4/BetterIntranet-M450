package ch.tbz.betterintranet.grades.api;

import java.util.List;

/** Result of {@link GetGradesOverviewQuery}. {@code overallAverage} is {@code null} when there are no grades. */
public record GradesOverview(Double overallAverage, List<SchoolPartGrades> schoolParts) {
}
