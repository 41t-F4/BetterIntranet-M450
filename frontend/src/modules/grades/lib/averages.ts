import type { GradesOverview, SchoolPart, SubjectGrades } from '../types'

/** One bar group of the averages chart. */
export interface AveragesChartDatum {
  schoolPart: SchoolPart
  label: string
  average: number | null
  reportGrade: number | null
}

/** Mean of the subjects' report grades, ignoring `null`s; `null` when no subject has one. */
export function reportGradeMean(subjects: readonly SubjectGrades[]): number | null {
  const reportGrades = subjects.map((subject) => subject.reportGrade).filter((grade) => grade !== null)
  if (reportGrades.length === 0) {
    return null
  }
  const sum = reportGrades.reduce((total, grade) => total + grade, 0)
  return Math.round((sum / reportGrades.length) * 100) / 100
}

/** Chart data with one entry per school part, in backend order. */
export function chartData(overview: GradesOverview): AveragesChartDatum[] {
  return overview.schoolParts.map((part) => ({
    schoolPart: part.schoolPart,
    label: part.label,
    average: part.average,
    reportGrade: reportGradeMean(part.subjects),
  }))
}
