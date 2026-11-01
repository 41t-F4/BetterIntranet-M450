import { describe, expect, it } from 'vitest'
import { holidaysFixture, lessonsFixture } from '../../../test/fixtures/timetable'
import type { Lesson } from '../types'
import { formatTime, groupLessonsByDay, holidayFor, parseTime } from './lessons'

function lesson(overrides: Partial<Lesson>): Lesson {
  return {
    date: '2026-11-02',
    start: '08:10:00',
    end: '09:45:00',
    subject: 'ENG',
    subjectName: 'Englisch',
    teacher: 'MID',
    room: 'AU-512',
    status: 'REGULAR',
    info: null,
    ...overrides,
  }
}

describe('parseTime', () => {
  it('parses HH:mm:ss', () => {
    expect(parseTime('08:10:00')).toEqual({ hours: 8, minutes: 10 })
  })

  it('parses HH:mm', () => {
    expect(parseTime('13:15')).toEqual({ hours: 13, minutes: 15 })
  })

  it('rejects anything else', () => {
    expect(() => parseTime('8:10')).toThrow()
    expect(() => parseTime('25:00')).toThrow()
    expect(() => parseTime('08:60')).toThrow()
    expect(() => parseTime('nope')).toThrow()
  })
})

describe('formatTime', () => {
  it('drops the seconds', () => {
    expect(formatTime('08:10:00')).toBe('08:10')
  })

  it('keeps HH:mm unchanged', () => {
    expect(formatTime('09:45')).toBe('09:45')
  })
})

describe('groupLessonsByDay', () => {
  const days = ['2026-11-02', '2026-11-03', '2026-11-04', '2026-11-05', '2026-11-06']

  it('creates one entry per requested day, in order, with an empty list for days without lessons', () => {
    const grouped = groupLessonsByDay([], days)

    expect([...grouped.keys()]).toEqual(days)
    expect([...grouped.values()].every((lessons) => lessons.length === 0)).toBe(true)
  })

  it('assigns every fixture lesson to its day', () => {
    const grouped = groupLessonsByDay(lessonsFixture, days)

    expect(grouped.get('2026-11-02')?.map((l) => l.subject)).toEqual(['117', 'M450'])
    expect(grouped.get('2026-11-03')?.map((l) => l.subject)).toEqual(['ENG', 'DEU'])
    expect(grouped.get('2026-11-04')?.map((l) => l.subject)).toEqual(['SPO'])
    expect(grouped.get('2026-11-05')?.map((l) => l.subject)).toEqual(['MAT'])
    expect(grouped.get('2026-11-06')?.map((l) => l.subject)).toEqual(['M320'])
  })

  it('sorts the lessons of a day by start time, regardless of input order and time format', () => {
    const grouped = groupLessonsByDay(
      [lesson({ start: '13:15', subject: 'C' }), lesson({ start: '10:05:00', subject: 'B' }), lesson({ start: '08:10', subject: 'A' })],
      ['2026-11-02'],
    )

    expect(grouped.get('2026-11-02')?.map((l) => l.subject)).toEqual(['A', 'B', 'C'])
  })

  it('drops lessons outside the requested days', () => {
    const grouped = groupLessonsByDay([lesson({ date: '2026-11-09' })], days)

    expect([...grouped.values()].flat()).toHaveLength(0)
  })
})

describe('holidayFor', () => {
  it('returns the holiday covering a date in the middle of it', () => {
    expect(holidayFor('2026-10-10', holidaysFixture)?.name).toBe('Herbstferien')
  })

  it('treats start and end date as inclusive', () => {
    expect(holidayFor('2026-10-05', holidaysFixture)?.name).toBe('Herbstferien')
    expect(holidayFor('2026-10-18', holidaysFixture)?.name).toBe('Herbstferien')
    expect(holidayFor('2026-10-04', holidaysFixture)).toBeUndefined()
    expect(holidayFor('2026-10-19', holidaysFixture)).toBeUndefined()
  })

  it('matches a one-day holiday', () => {
    expect(holidayFor('2026-11-05', holidaysFixture)?.name).toBe('Tag der Schule')
  })

  it('matches a holiday across a year boundary', () => {
    expect(holidayFor('2027-01-01', holidaysFixture)?.name).toBe('Weihnachtsferien')
  })

  it('returns undefined for a school day', () => {
    expect(holidayFor('2026-11-02', holidaysFixture)).toBeUndefined()
    expect(holidayFor('2026-11-02', [])).toBeUndefined()
  })
})
