import { describe, expect, it } from 'vitest'
import { formatDate, formatGrade, isInsufficient } from './format'

describe('formatGrade', () => {
  it('renders integers with one decimal', () => {
    expect(formatGrade(5)).toBe('5.0')
    expect(formatGrade(6)).toBe('6.0')
  })

  it('keeps a single decimal when that is exact', () => {
    expect(formatGrade(5.5)).toBe('5.5')
  })

  it('keeps two decimals', () => {
    expect(formatGrade(5.17)).toBe('5.17')
  })

  it('rounds to at most two decimals', () => {
    expect(formatGrade(4.875)).toBe('4.88')
    expect(formatGrade(5.333333)).toBe('5.33')
  })

  it('renders a dash for a missing value', () => {
    expect(formatGrade(null)).toBe('–')
  })
})

describe('isInsufficient', () => {
  it('is true below 4.0', () => {
    expect(isInsufficient(3.5)).toBe(true)
    expect(isInsufficient(3.99)).toBe(true)
  })

  it('is false at exactly 4.0', () => {
    expect(isInsufficient(4.0)).toBe(false)
  })

  it('is false above 4.0', () => {
    expect(isInsufficient(5.5)).toBe(false)
  })

  it('is false for a missing value', () => {
    expect(isInsufficient(null)).toBe(false)
  })
})

describe('formatDate', () => {
  it('formats an ISO date as Swiss date', () => {
    expect(formatDate('2026-10-28')).toBe('28.10.2026')
  })

  it('keeps leading zeros', () => {
    expect(formatDate('2026-01-05')).toBe('05.01.2026')
  })
})
