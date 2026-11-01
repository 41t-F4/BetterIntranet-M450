/** Types of the `GET /api/absences` contract. */

export type AbsenceStatus = 'OPEN' | 'EXCUSED' | 'UNEXCUSED'

/** One absence entry as returned by the backend. Times are `HH:mm:ss`, dates ISO (`YYYY-MM-DD`). */
export interface Absence {
  id: number
  date: string
  lessonStart: string
  lessonEnd: string
  subject: string
  teacher: string
  status: AbsenceStatus
  reason: string | null
  /** ISO local date-time, e.g. `2026-10-12T10:00:00`. */
  reportedAt: string
}

/** Counts over all absences of the user, independent of the status filter. */
export interface AbsencesSummary {
  total: number
  open: number
  excused: number
  unexcused: number
  totalLessons: number
}

/** Response of `GET /api/absences[?status=...]`. */
export interface AbsencesResponse {
  summary: AbsencesSummary
  absences: Absence[]
}
