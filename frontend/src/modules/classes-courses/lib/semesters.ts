import type { Course } from '../types'

/** Distinct semesters of the given courses, newest first (`HS 2026`, `FS 2026`, `HS 2025`, ...). */
export function distinctSemesters(_courses: readonly Course[]): string[] {
  throw new Error('Not implemented')
}

/** Courses of the given semester, or all courses when `semester` is `null`. */
export function filterBySemester(_courses: readonly Course[], _semester: string | null): Course[] {
  throw new Error('Not implemented')
}
