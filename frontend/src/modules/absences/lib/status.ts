import type { AbsenceStatus } from '../types'

/** All absence statuses in display order (filter options, summary tiles). */
export const ABSENCE_STATUSES: readonly AbsenceStatus[] = []

/** German label of an absence status, as shown in badges, tiles and the filter. */
export function statusLabel(status: AbsenceStatus): string {
  return status
}
