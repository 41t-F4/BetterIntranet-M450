/** Grades below this threshold count as insufficient («ungenügend»). */
export const INSUFFICIENT_THRESHOLD = 4.0

/** Placeholder for a missing grade or average. */
export const MISSING_VALUE = '–'

/**
 * Formats a grade or average for display: integers get one decimal (`5` → `5.0`), everything else
 * is rounded to at most two decimals (`5.17` → `5.17`), `null` becomes an en dash.
 */
export function formatGrade(grade: number | null): string {
  if (grade === null) {
    return MISSING_VALUE
  }
  const rounded = Math.round(grade * 100) / 100
  return Number.isInteger(rounded) ? rounded.toFixed(1) : String(rounded)
}

/** Whether a grade or average is below 4.0. `null` is never insufficient. */
export function isInsufficient(grade: number | null): boolean {
  return grade !== null && grade < INSUFFICIENT_THRESHOLD
}

/** Formats an ISO date (`2026-10-28`) as Swiss date (`28.10.2026`). */
export function formatDate(isoDate: string): string {
  const [year, month, day] = isoDate.split('-')
  return `${day}.${month}.${year}`
}
