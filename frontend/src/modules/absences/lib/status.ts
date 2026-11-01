import type { AbsenceStatus } from '../types'

/** All absence statuses in display order (filter options, summary tiles). */
export const ABSENCE_STATUSES: readonly AbsenceStatus[] = ['OPEN', 'EXCUSED', 'UNEXCUSED']

const LABELS: Record<AbsenceStatus, string> = {
  OPEN: 'Offen',
  EXCUSED: 'Entschuldigt',
  UNEXCUSED: 'Unentschuldigt',
}

/** German label of an absence status, as shown in badges, tiles and the filter. */
export function statusLabel(status: AbsenceStatus): string {
  return LABELS[status]
}

/** Whether a raw value (select option, query parameter) is one of the known statuses. */
export function isAbsenceStatus(value: string | null | undefined): value is AbsenceStatus {
  return ABSENCE_STATUSES.some((status) => status === value)
}
