import { describe, expect, it } from 'vitest'
import { addDays, formatDayLabel, formatWeekLabel, isoWeek, parseIsoDate, toIsoDate, weekRange } from './week'

describe('toIsoDate / parseIsoDate', () => {
  it('formats a local date with zero-padded month and day', () => {
    expect(toIsoDate(new Date(2026, 0, 5))).toBe('2026-01-05')
  })

  it('parses an ISO date as local midnight', () => {
    const date = parseIsoDate('2026-11-02')

    expect([date.getFullYear(), date.getMonth(), date.getDate()]).toEqual([2026, 10, 2])
    expect([date.getHours(), date.getMinutes()]).toEqual([0, 0])
  })

  it('round-trips', () => {
    expect(toIsoDate(parseIsoDate('2026-02-28'))).toBe('2026-02-28')
  })

  it('rejects anything that is not YYYY-MM-DD', () => {
    expect(() => parseIsoDate('2.11.2026')).toThrow('Ungültiges Datum')
    expect(() => parseIsoDate('')).toThrow('Ungültiges Datum')
  })
})

describe('addDays', () => {
  it('adds days within a month', () => {
    expect(addDays('2026-11-02', 4)).toBe('2026-11-06')
  })

  it('subtracts days across a month boundary', () => {
    expect(addDays('2026-11-02', -7)).toBe('2026-10-26')
  })

  it('adds days across a year boundary', () => {
    expect(addDays('2026-12-28', 7)).toBe('2027-01-04')
  })
})

describe('weekRange', () => {
  it('returns Monday to Friday of the week containing a Wednesday', () => {
    expect(weekRange('2026-11-04')).toEqual({
      from: '2026-11-02',
      to: '2026-11-06',
      days: ['2026-11-02', '2026-11-03', '2026-11-04', '2026-11-05', '2026-11-06'],
    })
  })

  it('accepts a Date', () => {
    expect(weekRange(new Date(2026, 10, 4, 15, 30))).toMatchObject({ from: '2026-11-02', to: '2026-11-06' })
  })

  it('returns the same week for the Monday itself and for the Friday', () => {
    expect(weekRange('2026-11-02').from).toBe('2026-11-02')
    expect(weekRange('2026-11-06').from).toBe('2026-11-02')
  })

  it('assigns Saturday and Sunday to the preceding Monday (ISO week)', () => {
    expect(weekRange('2026-11-07').from).toBe('2026-11-02')
    expect(weekRange('2026-11-01').from).toBe('2026-10-26')
  })

  it('spans a month boundary', () => {
    expect(weekRange('2026-12-02')).toMatchObject({ from: '2026-11-30', to: '2026-12-04' })
  })

  it('spans a year boundary', () => {
    expect(weekRange('2027-01-01')).toMatchObject({ from: '2026-12-28', to: '2027-01-01' })
  })
})

describe('isoWeek', () => {
  it('returns KW 45 for 2. November 2026', () => {
    expect(isoWeek('2026-11-02')).toEqual({ week: 45, year: 2026 })
  })

  it('returns KW 1 for 1. Januar 2026 (a Thursday)', () => {
    expect(isoWeek('2026-01-01')).toEqual({ week: 1, year: 2026 })
  })

  it('assigns 29. Dezember 2025 to KW 1 of 2026', () => {
    expect(isoWeek('2025-12-29')).toEqual({ week: 1, year: 2026 })
  })

  it('returns KW 53 for 31. Dezember 2026 (2026 has 53 ISO weeks)', () => {
    expect(isoWeek('2026-12-31')).toEqual({ week: 53, year: 2026 })
  })

  it('assigns 1. Januar 2027 (a Friday) to KW 53 of 2026', () => {
    expect(isoWeek('2027-01-01')).toEqual({ week: 53, year: 2026 })
  })

  it('returns KW 1 for 4. Januar 2027', () => {
    expect(isoWeek(new Date(2027, 0, 4))).toEqual({ week: 1, year: 2027 })
  })
})

describe('formatWeekLabel', () => {
  it('formats a week inside one month in German', () => {
    expect(formatWeekLabel('2026-11-02')).toBe('KW 45 · 2. Nov. – 6. Nov. 2026')
  })

  it('formats a week across a month boundary', () => {
    expect(formatWeekLabel('2026-11-30')).toBe('KW 49 · 30. Nov. – 4. Dez. 2026')
  })

  it('repeats the year when the week crosses a year boundary', () => {
    expect(formatWeekLabel('2026-12-28')).toBe('KW 53 · 28. Dez. 2026 – 1. Jan. 2027')
  })

  it('uses the unabbreviated German names for März, Mai, Juni and Juli', () => {
    expect(formatWeekLabel('2026-03-02')).toBe('KW 10 · 2. März – 6. März 2026')
    expect(formatWeekLabel('2026-06-29')).toBe('KW 27 · 29. Juni – 3. Juli 2026')
  })
})

describe('formatDayLabel', () => {
  it('formats the German weekday with day and month', () => {
    expect(formatDayLabel('2026-11-02')).toBe('Montag, 2. Nov.')
    expect(formatDayLabel('2026-11-06')).toBe('Freitag, 6. Nov.')
  })
})
