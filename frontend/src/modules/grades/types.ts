/** Types of the `GET /api/grades` contract. */

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
  /** Semester label, e.g. `HS 2026` or `FS 2026`. */
  semester: string
  /** Weighted average, `null` when it cannot be computed. */
  average: number | null
  /** Swiss-rounded report grade, `null` when there is no average. */
  reportGrade: number | null
  /** Sorted by date, newest first. */
  grades: Grade[]
}

/** Response of `GET /api/grades`. */
export interface GradesOverview {
  overallAverage: number | null
  /** Sorted newest semester first, then subject A–Z. */
  subjects: SubjectGrades[]
}
