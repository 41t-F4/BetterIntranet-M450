import { describe, expect, it } from 'vitest'
import { courseCountLabel, roleLabel } from './labels'

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
    expect(courseCountLabel(4)).toBe('4 Kurse')
  })
})
