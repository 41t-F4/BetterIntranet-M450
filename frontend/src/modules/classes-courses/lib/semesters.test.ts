import { describe, expect, it } from 'vitest'
import { myCoursesFixture } from '../../../test/fixtures/courses'
import type { Course } from '../types'
import { distinctSemesters, filterBySemester } from './semesters'

function course(id: string, semester: string): Course {
  return { id, code: id, name: id, teacher: 'X', semester, lessonsPerWeek: 1, room: 'R', schoolPart: 'BMS' }
}

describe('distinctSemesters', () => {
  it('returns each semester once', () => {
    expect(distinctSemesters(myCoursesFixture)).toHaveLength(2)
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

    expect(filtered).toHaveLength(4)
    expect(filtered.every((item) => item.semester === 'HS 2026')).toBe(true)
  })

  it('returns all courses when no semester is selected', () => {
    expect(filterBySemester(myCoursesFixture, null)).toEqual(myCoursesFixture)
  })
})
