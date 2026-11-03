import { describe, expect, it } from 'vitest'
import { emptyGradesOverviewFixture, gradesOverviewFixture } from '../../../test/fixtures/grades'
import type { SubjectGrades } from '../types'
import { chartData, semesterAverage, semesters, shortSubjectLabel, subjectsOfSemester } from './averages'

function subject(name: string, average: number | null, reportGrade: number | null = average): SubjectGrades {
  return { subject: name, semester: 'HS 2026', average, reportGrade, grades: [] }
}

describe('semesters', () => {
  it('lists the distinct semesters in response order, newest first', () => {
    expect(semesters(gradesOverviewFixture)).toEqual(['HS 2026', 'FS 2026'])
  })

  it('is empty without subjects', () => {
    expect(semesters(emptyGradesOverviewFixture)).toEqual([])
  })
})

describe('subjectsOfSemester', () => {
  it('returns the subjects of the given semester in response order', () => {
    expect(subjectsOfSemester(gradesOverviewFixture, 'FS 2026').map((entry) => entry.subject)).toEqual([
      '320 Objektorientiert programmieren',
      '322 Benutzerschnittstellen entwerfen und implementieren',
      'Allgemeinbildung Gesellschaft',
      'Englisch',
    ])
  })

  it('is empty for an unknown semester', () => {
    expect(subjectsOfSemester(gradesOverviewFixture, 'HS 2020')).toEqual([])
  })
})

describe('semesterAverage', () => {
  it('averages the subject averages of a semester', () => {
    expect(semesterAverage([subject('A', 5.0), subject('B', 5.5)])).toBe(5.25)
  })

  it('ignores subjects without an average', () => {
    expect(semesterAverage([subject('A', 5.0), subject('B', null), subject('C', 6.0)])).toBe(5.5)
  })

  it('rounds to two decimals', () => {
    expect(semesterAverage(subjectsOfSemester(gradesOverviewFixture, 'FS 2026'))).toBe(5.08)
  })

  it('is null when no subject has an average', () => {
    expect(semesterAverage([subject('A', null)])).toBeNull()
    expect(semesterAverage([])).toBeNull()
  })
})

describe('shortSubjectLabel', () => {
  it('abbreviates ICT modules to «M» plus the module number', () => {
    expect(shortSubjectLabel('450 Applikationen testen')).toBe('M450')
    expect(shortSubjectLabel('320 Objektorientiert programmieren')).toBe('M320')
  })

  it('abbreviates the general education subject to «ABU»', () => {
    expect(shortSubjectLabel('Allgemeinbildung Gesellschaft')).toBe('ABU')
  })

  it('keeps every other subject name unchanged', () => {
    expect(shortSubjectLabel('Englisch')).toBe('Englisch')
  })
})

describe('chartData', () => {
  it('creates one entry per subject with short label, average and report grade', () => {
    const data = chartData(subjectsOfSemester(gradesOverviewFixture, 'HS 2026'))

    expect(data).toEqual([
      { subject: '450 Applikationen testen', label: 'M450', average: 5.0, reportGrade: 5.0 },
      { subject: 'Allgemeinbildung Gesellschaft', label: 'ABU', average: 4.0, reportGrade: 4.0 },
      { subject: 'Englisch', label: 'Englisch', average: 5.88, reportGrade: 6.0 },
    ])
  })

  it('passes null averages through', () => {
    expect(chartData([subject('320 Objektorientiert programmieren', null)])).toEqual([
      { subject: '320 Objektorientiert programmieren', label: 'M320', average: null, reportGrade: null },
    ])
  })

  it('is empty without subjects', () => {
    expect(chartData([])).toEqual([])
  })
})
