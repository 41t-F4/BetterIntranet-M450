import { describe, expect, it } from 'vitest'
import { myCoursesFixture } from '../../../test/fixtures/courses'
import type { Course } from '../types'
import { groupBySchoolPart } from './groupBySchoolPart'

function course(id: string, schoolPart: Course['schoolPart']): Course {
  return { id, code: id, name: id, teacher: 'X', semester: 'HS 2026', lessonsPerWeek: 1, room: 'R', schoolPart }
}

describe('groupBySchoolPart', () => {
  it('groups courses by school part in the fixed order BMS, TBZ, UEK', () => {
    const groups = groupBySchoolPart(myCoursesFixture)

    expect(groups.map((group) => group.schoolPart)).toEqual(['BMS', 'TBZ', 'UEK'])
    expect(groups.map((group) => group.courses.length)).toEqual([3, 2, 2])
  })

  it('keeps the fixed order even when the input is unsorted', () => {
    const groups = groupBySchoolPart([course('u', 'UEK'), course('t', 'TBZ'), course('b', 'BMS')])

    expect(groups.map((group) => group.schoolPart)).toEqual(['BMS', 'TBZ', 'UEK'])
  })

  it('keeps the input order of the courses within a group', () => {
    const groups = groupBySchoolPart([course('b2', 'BMS'), course('b1', 'BMS')])

    expect(groups[0]?.courses.map((item) => item.id)).toEqual(['b2', 'b1'])
  })

  it('omits school parts without courses', () => {
    const groups = groupBySchoolPart([course('t', 'TBZ')])

    expect(groups.map((group) => group.schoolPart)).toEqual(['TBZ'])
  })

  it('returns no groups for an empty list', () => {
    expect(groupBySchoolPart([])).toEqual([])
  })
})
