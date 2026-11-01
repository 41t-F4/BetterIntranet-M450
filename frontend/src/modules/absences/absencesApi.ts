import { apiGet } from '../../shared/api/client'
import type { AbsenceStatus, AbsencesResponse } from './types'

/** Base path of the endpoint in the backend. */
export const ABSENCES_API = '/api/absences'

/** Loads the absences of the logged-in user, optionally narrowed to one status. Throws `ApiError` on failure. */
export function fetchAbsences(status?: AbsenceStatus): Promise<AbsencesResponse> {
  return apiGet<AbsencesResponse>(ABSENCES_API, status === undefined ? undefined : { status })
}
