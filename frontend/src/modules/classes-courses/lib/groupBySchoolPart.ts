import type { Course, SchoolPart } from '../types'

/** Courses of one school part, in the order they were delivered. */
export interface CourseGroup {
  schoolPart: SchoolPart
  courses: Course[]
}

/** Groups courses by school part in the fixed order BMS, TBZ, UEK; empty school parts are omitted. */
export function groupBySchoolPart(_courses: readonly Course[]): CourseGroup[] {
  throw new Error('Not implemented')
}
