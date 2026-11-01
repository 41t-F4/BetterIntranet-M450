import { describe, expect, it } from 'vitest'
import { courseCountLabel, roleLabel, schoolPartLabel } from './labels'

describe('schoolPartLabel', () => {
  it('maps UEK to the German umlaut spelling', () => {
    expect(schoolPartLabel('UEK')).toBe('ÜK')
  })

  it('keeps BMS and TBZ as they are', () => {
    expect(schoolPartLabel('BMS')).toBe('BMS')
    expect(schoolPartLabel('TBZ')).toBe('TBZ')
  })
})

describe('roleLabel', () => {
  it('translates the backend roles to German', () => {
    expect(roleLabel('STUDENT')).toBe('Lernende/r')
    expect(roleLabel('TEACHER')).toBe('Lehrperson')
    expect(roleLabel('ADMIN')).toBe('Administrator/in')
  })
})

describe('courseCountLabel', () => {
  it('uses the singular for one course', () => {
    expect(courseCountLabel(1)).toBe('1 Kurs')
  })

  it('uses the plural otherwise', () => {
    expect(courseCountLabel(0)).toBe('0 Kurse')
    expect(courseCountLabel(6)).toBe('6 Kurse')
  })
})
