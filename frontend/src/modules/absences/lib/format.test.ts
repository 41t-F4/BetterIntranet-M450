import { describe, expect, it } from 'vitest'
import { formatDate, formatDateTime, formatTimeRange } from './format'

describe('formatDate', () => {
  it('formats an ISO date as Swiss date', () => {
    expect(formatDate('2026-10-12')).toBe('12.10.2026')
  })

  it('keeps leading zeros of day and month', () => {
    expect(formatDate('2026-01-05')).toBe('05.01.2026')
  })
})

describe('formatDateTime', () => {
  it('formats an ISO date-time as Swiss date with HH:mm', () => {
    expect(formatDateTime('2026-10-12T10:00:00')).toBe('12.10.2026 10:00')
  })

  it('drops the seconds', () => {
    expect(formatDateTime('2026-08-31T11:45:59')).toBe('31.08.2026 11:45')
  })

  it('accepts a date-time without seconds', () => {
    expect(formatDateTime('2026-08-31T11:45')).toBe('31.08.2026 11:45')
  })
})

describe('formatTimeRange', () => {
  it('turns HH:mm:ss times into a HH:mm range', () => {
    expect(formatTimeRange('08:10:00', '09:45:00')).toBe('08:10–09:45')
  })

  it('accepts times that already are HH:mm', () => {
    expect(formatTimeRange('13:15', '14:50')).toBe('13:15–14:50')
  })
})
