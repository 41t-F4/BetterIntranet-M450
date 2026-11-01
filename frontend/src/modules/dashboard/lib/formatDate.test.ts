import { describe, expect, it } from 'vitest'
import { formatDate, formatDateRange } from './formatDate'

describe('formatDate', () => {
  it('formats an ISO date as DD.MM.YYYY', () => {
    expect(formatDate('2026-10-28')).toBe('28.10.2026')
  })

  it('keeps leading zeros of day and month', () => {
    expect(formatDate('2027-01-02')).toBe('02.01.2027')
  })

  it('does not shift the day regardless of the time zone', () => {
    expect(formatDate('2026-12-31')).toBe('31.12.2026')
  })

  it('rejects a value that is not an ISO date', () => {
    expect(() => formatDate('28.10.2026')).toThrow('Ungültiges Datum')
  })
})

describe('formatDateRange', () => {
  it('shows a single date for a one-day entry', () => {
    expect(formatDateRange('2026-11-19', '2026-11-19')).toBe('19.11.2026')
  })

  it('shows start and end separated by an en dash for a multi-day entry', () => {
    expect(formatDateRange('2026-12-21', '2027-01-02')).toBe('21.12.2026 – 02.01.2027')
  })
})
