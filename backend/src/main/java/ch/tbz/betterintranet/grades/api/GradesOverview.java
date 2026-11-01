package ch.tbz.betterintranet.grades.api;

import java.util.List;

/**
 * Result of {@link GetGradesOverviewQuery}: the payload of {@code GET /api/grades}.
 *
 * <p>All averages in this structure are rounded half-up to two decimals and are {@code null} (JSON
 * {@code null}) when nothing can be averaged, so that the frontend can distinguish "no grades yet" from a
 * real value.
 *
 * @param overallAverage plain mean of the subject averages, {@code null} when no subject has one
 * @param subjects       one entry per subject and semester, newest semester first ({@code HS} before
 *                       {@code FS} of the same year), then by subject name
 */
public record GradesOverview(Double overallAverage, List<SubjectGrades> subjects) {
}
