import { describe, expect, it } from 'vitest'
import { gradesOverviewFixture } from '../../../test/fixtures/grades'
import type { GradesOverview, SubjectGrades } from '../types'
import { chartData, reportGradeMean } from './averages'

function subject(reportGrade: number | null): SubjectGrades {
  return { subject: 'Fach', semester: 'HS 2026', average: reportGrade, reportGrade, grades: [] }
}

describe('reportGradeMean', () => {
  it('averages the report grades of all subjects', () => {
    expect(reportGradeMean([subject(5.0), subject(5.5)])).toBe(5.25)
  })

  it('ignores subjects without a report grade', () => {
    expect(reportGradeMean([subject(5.0), subject(null), subject(6.0)])).toBe(5.5)
  })

  it('rounds to two decimals', () => {
    expect(reportGradeMean([subject(5.0), subject(4.5), subject(4.5)])).toBe(4.67)
  })

  it('is null when no subject has a report grade', () => {
    expect(reportGradeMean([subject(null)])).toBeNull()
    expect(reportGradeMean([])).toBeNull()
  })
})

describe('chartData', () => {
  it('creates one entry per school part in backend order', () => {
    const data = chartData(gradesOverviewFixture)

    expect(data.map((entry) => entry.label)).toEqual(['BMS', 'TBZ', 'ÜK'])
    expect(data.map((entry) => entry.schoolPart)).toEqual(['BMS', 'TBZ', 'UEK'])
  })

  it('uses the school part average and the mean of the report grades', () => {
    const [bms] = chartData(gradesOverviewFixture)

    expect(bms).toEqual({ schoolPart: 'BMS', label: 'BMS', average: 4.88, reportGrade: 5.0 })
  })

  it('passes null averages through', () => {
    const overview: GradesOverview = {
      overallAverage: null,
      schoolParts: [{ schoolPart: 'TBZ', label: 'TBZ', average: null, subjects: [subject(null)] }],
    }

    expect(chartData(overview)).toEqual([{ schoolPart: 'TBZ', label: 'TBZ', average: null, reportGrade: null }])
  })

  it('is empty without school parts', () => {
    expect(chartData({ overallAverage: null, schoolParts: [] })).toEqual([])
  })
})
