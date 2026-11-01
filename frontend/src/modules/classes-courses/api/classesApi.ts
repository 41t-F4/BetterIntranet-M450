import { apiGet } from '../../../shared/api/client'
import type { SchoolClass } from '../types'

/** Loads the classes of the logged-in user (`GET /api/classes`). */
export function fetchMyClasses(): Promise<SchoolClass[]> {
  return apiGet<SchoolClass[]>('/api/classes')
}
