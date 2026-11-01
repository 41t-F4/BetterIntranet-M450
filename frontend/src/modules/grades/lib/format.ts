/** Grades below this threshold count as insufficient («ungenügend»). */
export const INSUFFICIENT_THRESHOLD = 4.0

/**
 * Formats a grade or average for display: integers get one decimal (`5` → `5.0`), everything else
 * is rounded to at most two decimals (`5.17` → `5.17`), `null` becomes an en dash.
 */
export function formatGrade(_grade: number | null): string {
  throw new Error('not implemented')
}

/** Whether a grade or average is below 4.0. `null` is never insufficient. */
export function isInsufficient(_grade: number | null): boolean {
  throw new Error('not implemented')
}

/** Formats an ISO date (`2026-10-28`) as Swiss date (`28.10.2026`). */
export function formatDate(_isoDate: string): string {
  throw new Error('not implemented')
}
