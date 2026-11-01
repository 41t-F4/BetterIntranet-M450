import { apiGet } from '../../shared/api/client'
import type { GradesOverview } from './types'

/** Loads the grades of the logged-in user including the computed averages. */
export function fetchGrades(): Promise<GradesOverview> {
  return apiGet<GradesOverview>('/api/grades')
}
