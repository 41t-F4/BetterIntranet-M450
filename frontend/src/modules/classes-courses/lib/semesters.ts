import type { Course } from '../types'

/** Term order within a year: autumn semester (HS) starts after the spring semester (FS). */
const TERM_RANK: Readonly<Record<string, number>> = { FS: 0, HS: 1 }

/** Distinct semesters of the given courses, newest first (`HS 2026`, `FS 2026`, `HS 2025`, ...). */
export function distinctSemesters(courses: readonly Course[]): string[] {
  const semesters = [...new Set(courses.map((course) => course.semester))]
  return semesters.sort((a, b) => semesterKey(b) - semesterKey(a))
}

/** Courses of the given semester, or all courses when `semester` is `null`. */
export function filterBySemester(courses: readonly Course[], semester: string | null): Course[] {
  return semester === null ? [...courses] : courses.filter((course) => course.semester === semester)
}

/** Courses of one semester. */
export interface SemesterGroup {
  semester: string
  courses: Course[]
}

/** Groups courses by semester, newest semester first, keeping the input order within a group. */
export function groupBySemester(courses: readonly Course[]): SemesterGroup[] {
  return distinctSemesters(courses).map((semester) => ({ semester, courses: filterBySemester(courses, semester) }))
}

/** Sort key of a semester such as `HS 2026`: year first, then term. Unknown terms sort before known ones. */
function semesterKey(semester: string): number {
  const [term = '', year = '0'] = semester.split(' ')
  return Number.parseInt(year, 10) * 10 + (TERM_RANK[term] ?? -1)
}
