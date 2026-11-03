import type { GradesOverview, SubjectGrades } from '../types'

/** One bar group of the averages chart: a subject of the selected semester. */
export interface AveragesChartDatum {
  subject: string
  /** Short label for the x-axis, see {@link shortSubjectLabel}. */
  label: string
  average: number | null
  reportGrade: number | null
}

/** Subjects with an explicit abbreviation; everything not listed (and not an ICT module) stays as is. */
const SUBJECT_ABBREVIATIONS: Readonly<Record<string, string>> = {
  'Allgemeinbildung Gesellschaft': 'ABU',
}

const ICT_MODULE = /^(\d+)\s/

/** Distinct semesters in response order (the backend sorts newest first). */
export function semesters(overview: GradesOverview): string[] {
  return [...new Set(overview.subjects.map((subject) => subject.semester))]
}

/** Subjects of one semester in response order (A–Z). */
export function subjectsOfSemester(overview: GradesOverview, semester: string): SubjectGrades[] {
  return overview.subjects.filter((subject) => subject.semester === semester)
}

/** Mean of the subject averages rounded to two decimals, ignoring `null`s; `null` when none has one. */
export function semesterAverage(subjects: readonly SubjectGrades[]): number | null {
  const averages = subjects.map((subject) => subject.average).filter((average) => average !== null)
  if (averages.length === 0) {
    return null
  }
  const sum = averages.reduce((total, average) => total + average, 0)
  return Math.round((sum / averages.length) * 100) / 100
}

/**
 * Short chart label of a subject: ICT modules (`450 Applikationen testen`) become `M450`, subjects
 * with an explicit abbreviation use it (`Allgemeinbildung Gesellschaft` → `ABU`), all others keep their name.
 */
export function shortSubjectLabel(subject: string): string {
  const module = ICT_MODULE.exec(subject)
  if (module) {
    return `M${module[1]}`
  }
  return SUBJECT_ABBREVIATIONS[subject] ?? subject
}

/** Chart data with one entry per subject, in the given order. */
export function chartData(subjects: readonly SubjectGrades[]): AveragesChartDatum[] {
  return subjects.map((subject) => ({
    subject: subject.subject,
    label: shortSubjectLabel(subject.subject),
    average: subject.average,
    reportGrade: subject.reportGrade,
  }))
}
