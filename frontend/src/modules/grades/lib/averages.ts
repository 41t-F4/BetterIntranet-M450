import type { GradesOverview, SubjectGrades } from '../types'

/** One bar group of the averages chart. */
export interface AveragesChartDatum {
  subject: string
  label: string
  average: number | null
  reportGrade: number | null
}

export function semesters(_overview: GradesOverview): string[] {
  return []
}

export function subjectsOfSemester(_overview: GradesOverview, _semester: string): SubjectGrades[] {
  return []
}

export function semesterAverage(_subjects: readonly SubjectGrades[]): number | null {
  return null
}

export function shortSubjectLabel(subject: string): string {
  return subject
}

export function chartData(_subjects: readonly SubjectGrades[]): AveragesChartDatum[] {
  return []
}
