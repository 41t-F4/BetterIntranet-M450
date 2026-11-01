/** Types of the `GET /api/grades` contract. */

/** School part identifiers as reported by the backend. */
export type SchoolPart = 'BMS' | 'TBZ' | 'UEK'

/** A single graded exam or assignment. */
export interface Grade {
  id: number
  title: string
  /** ISO date (`YYYY-MM-DD`). */
  date: string
  grade: number
  weight: number
}

/** All grades of one subject in one semester. */
export interface SubjectGrades {
  subject: string
  semester: string
  /** Weighted average, `null` when it cannot be computed. */
  average: number | null
  /** Swiss-rounded report grade, `null` when there is no average. */
  reportGrade: number | null
  /** Sorted by date, newest first. */
  grades: Grade[]
}

/** Grades of one school part (BMS, TBZ, ÜK). */
export interface SchoolPartGrades {
  schoolPart: SchoolPart
  /** Display label (`BMS`, `TBZ`, `ÜK`). */
  label: string
  average: number | null
  /** Sorted by subject A–Z, then newest semester first. */
  subjects: SubjectGrades[]
}

/** Response of `GET /api/grades`. */
export interface GradesOverview {
  overallAverage: number | null
  /** Only school parts with grades, ordered BMS, TBZ, UEK. */
  schoolParts: SchoolPartGrades[]
}
