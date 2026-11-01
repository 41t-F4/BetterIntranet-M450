import { fetchCourses } from '../api/coursesApi'
import type { Course } from '../types'
import { useModuleResource, type ModuleResourceState } from './useModuleResource'

/** Stable loader for all courses; the semester filter is applied client-side on this list. */
function fetchAllCourses(): Promise<Course[]> {
  return fetchCourses()
}

/** All courses of the logged-in user; skips the request while the `courses` module is disabled. */
export function useMyCourses(): ModuleResourceState<Course[]> {
  return useModuleResource('courses', fetchAllCourses)
}
