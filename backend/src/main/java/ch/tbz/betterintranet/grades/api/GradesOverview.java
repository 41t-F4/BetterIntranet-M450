package ch.tbz.betterintranet.grades.api;

import java.util.List;

/**
 * Result of {@link GetGradesOverviewQuery}: the payload of {@code GET /api/grades}.
 *
 * <p>All averages in this structure are rounded half-up to two decimals and are {@code null} (JSON
 * {@code null}) when nothing can be averaged, so that the frontend can distinguish "no grades yet" from a
 * real value.
 *
 * @param overallAverage plain mean of the school part averages, {@code null} when no school part has one
 * @param schoolParts    school parts that have at least one grade, in the order BMS, TBZ, ÜK
 */
public record GradesOverview(Double overallAverage, List<SchoolPartGrades> schoolParts) {
}
