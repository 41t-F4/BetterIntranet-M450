import type { GradesOverview, SchoolPart, SubjectGrades } from '../types'

/** One bar group of the averages chart. */
export interface AveragesChartDatum {
  schoolPart: SchoolPart
  label: string
  average: number | null
  reportGrade: number | null
}

/** Mean of the subjects' report grades, ignoring `null`s; `null` when no subject has one. */
export function reportGradeMean(_subjects: readonly SubjectGrades[]): number | null {
  throw new Error('not implemented')
}

/** Chart data with one entry per school part, in backend order. */
export function chartData(_overview: GradesOverview): AveragesChartDatum[] {
  throw new Error('not implemented')
}
