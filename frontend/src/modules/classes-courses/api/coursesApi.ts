import type { Course } from '../types'

/** Loads the courses of the logged-in user (`GET /api/courses`), optionally restricted to a semester. */
export function fetchCourses(_semester?: string): Promise<Course[]> {
  return Promise.reject(new Error('Not implemented'))
}
