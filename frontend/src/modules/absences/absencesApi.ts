import { apiGet } from '../../shared/api/client'
import type { AbsenceStatus, AbsencesResponse } from './types'

/** Base path of the endpoint in the backend. */
export const ABSENCES_API = '/api/absences'

/** URL of `GET /api/absences`, optionally narrowed to one status. */
export function absencesUrl(status?: AbsenceStatus): string {
  if (status === undefined) {
    return ABSENCES_API
  }
  const params = new URLSearchParams({ status })
  return `${ABSENCES_API}?${params.toString()}`
}

/** Loads the absences of the logged-in user. Throws `ApiError` for non-2xx responses. */
export function fetchAbsences(status?: AbsenceStatus): Promise<AbsencesResponse> {
  return apiGet<AbsencesResponse>(absencesUrl(status))
}
