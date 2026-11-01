import type { SchoolClass } from '../types'

/** Loads the classes of the logged-in user (`GET /api/classes`). */
export function fetchMyClasses(): Promise<SchoolClass[]> {
  return Promise.reject(new Error('Not implemented'))
}
