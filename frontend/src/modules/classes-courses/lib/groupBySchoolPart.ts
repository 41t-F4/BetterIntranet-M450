import type { Course, SchoolPart } from '../types'

/** Courses of one school part, in the order they were delivered. */
export interface CourseGroup {
  schoolPart: SchoolPart
  courses: Course[]
}

/** Display order of the school parts, as on the «Marks» board. */
export const SCHOOL_PART_ORDER: readonly SchoolPart[] = ['BMS', 'TBZ', 'UEK']

/** Groups courses by school part in the fixed order BMS, TBZ, UEK; empty school parts are omitted. */
export function groupBySchoolPart(courses: readonly Course[]): CourseGroup[] {
  return SCHOOL_PART_ORDER.map((schoolPart) => ({
    schoolPart,
    courses: courses.filter((course) => course.schoolPart === schoolPart),
  })).filter((group) => group.courses.length > 0)
}
