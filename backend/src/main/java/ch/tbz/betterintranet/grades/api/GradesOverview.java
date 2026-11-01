package ch.tbz.betterintranet.grades.api;

import java.util.List;

/**
 * Result of {@link GetGradesOverviewQuery}: the payload of {@code GET /api/grades}.
 *
 * @param overallAverage plain mean of the subject averages, {@code null} when no subject has one
 * @param subjects       one entry per subject and semester
 */
public record GradesOverview(Double overallAverage, List<SubjectGrades> subjects) {
}
