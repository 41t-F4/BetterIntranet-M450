import { apiGet } from '../../../shared/api/client'
import type { Course } from '../types'

/** Base path of the endpoint in the backend. */
export const COURSES_API = '/api/courses'

/** Loads the courses of the logged-in user (`GET /api/courses`), optionally restricted to a semester. */
export function fetchCourses(semester?: string): Promise<Course[]> {
  return apiGet<Course[]>(COURSES_API, semester === undefined ? undefined : { semester })
}
