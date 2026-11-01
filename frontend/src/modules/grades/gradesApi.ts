import { apiGet } from '../../shared/api/client'
import type { GradesOverview } from './types'

/** Base path of the endpoint in the backend. */
export const GRADES_API = '/api/grades'

/** Loads the grades of the logged-in user including the computed averages. */
export function fetchGrades(): Promise<GradesOverview> {
  return apiGet<GradesOverview>(GRADES_API)
}
