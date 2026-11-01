import type { UserRole } from '../../shared/api/types'

/** School part a course belongs to, as delivered by `GET /api/courses`. */
export type SchoolPart = 'BMS' | 'TBZ' | 'UEK'

/** One entry of `GET /api/classes`. */
export interface SchoolClass {
  id: number
  shortName: string
  longName: string
  department: string
  classTeacher: string
  schoolYear: string
  role: UserRole
}

/** One entry of `GET /api/courses`. */
export interface Course {
  id: string
  code: string
  name: string
  teacher: string
  semester: string
  lessonsPerWeek: number
  room: string
  /** @deprecated No longer delivered by `GET /api/courses`; the intranet only covers the TBZ. */
  schoolPart?: SchoolPart
}
