import { describe, expect, it } from 'vitest'
import { myCoursesFixture } from '../../../test/fixtures/courses'
import type { Course } from '../types'
import { distinctSemesters, filterBySemester, groupBySemester } from './semesters'

function course(id: string, semester: string): Course {
  return { id, code: id, name: id, teacher: 'X', semester, lessonsPerWeek: 1, room: 'R' }
}

describe('distinctSemesters', () => {
  it('returns each semester once, newest first', () => {
    expect(distinctSemesters(myCoursesFixture)).toEqual(['HS 2026', 'FS 2026'])
  })

  it('sorts semesters newest first, autumn (HS) before spring (FS) of the same year', () => {
    const courses = [course('a', 'FS 2026'), course('b', 'HS 2025'), course('c', 'HS 2026'), course('d', 'FS 2025')]

    expect(distinctSemesters(courses)).toEqual(['HS 2026', 'FS 2026', 'HS 2025', 'FS 2025'])
  })

  it('returns an empty list when there are no courses', () => {
    expect(distinctSemesters([])).toEqual([])
  })
})

describe('filterBySemester', () => {
  it('returns only the courses of the given semester', () => {
    const filtered = filterBySemester(myCoursesFixture, 'HS 2026')

    expect(filtered.map((item) => item.code)).toEqual(['306', '450', 'ENG', 'GES'])
    expect(filtered.every((item) => item.semester === 'HS 2026')).toBe(true)
  })

  it('returns all courses when no semester is selected', () => {
    expect(filterBySemester(myCoursesFixture, null)).toEqual(myCoursesFixture)
  })
})

describe('groupBySemester', () => {
  it('groups the courses by semester, newest semester first', () => {
    const groups = groupBySemester(myCoursesFixture)

    expect(groups.map((group) => group.semester)).toEqual(['HS 2026', 'FS 2026'])
    expect(groups.map((group) => group.courses.map((item) => item.code))).toEqual([
      ['306', '450', 'ENG', 'GES'],
      ['320', '322', 'ENG', 'GES'],
    ])
  })

  it('orders the groups newest first even when the input is unsorted', () => {
    const groups = groupBySemester([course('a', 'FS 2026'), course('b', 'HS 2025'), course('c', 'HS 2026')])

    expect(groups.map((group) => group.semester)).toEqual(['HS 2026', 'FS 2026', 'HS 2025'])
  })

  it('keeps the input order of the courses within a semester', () => {
    const groups = groupBySemester([course('b', 'HS 2026'), course('a', 'HS 2026')])

    expect(groups).toEqual([{ semester: 'HS 2026', courses: [course('b', 'HS 2026'), course('a', 'HS 2026')] }])
  })

  it('returns no groups for an empty list', () => {
    expect(groupBySemester([])).toEqual([])
  })
})
