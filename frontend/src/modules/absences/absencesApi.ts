import type { AbsenceStatus, AbsencesResponse } from './types'

/** URL of `GET /api/absences`, optionally narrowed to one status. */
export function absencesUrl(status?: AbsenceStatus): string {
  return `/api/absences/${status ?? ''}`
}

/** Loads the absences of the logged-in user. Throws `ApiError` for non-2xx responses. */
export function fetchAbsences(_status?: AbsenceStatus): Promise<AbsencesResponse> {
  return Promise.reject(new Error('not implemented'))
}
