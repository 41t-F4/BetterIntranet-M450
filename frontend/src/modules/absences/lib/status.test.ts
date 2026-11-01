import { describe, expect, it } from 'vitest'
import { ABSENCE_STATUSES, isAbsenceStatus, statusLabel } from './status'

describe('statusLabel', () => {
  it('labels OPEN as «Offen»', () => {
    expect(statusLabel('OPEN')).toBe('Offen')
  })

  it('labels EXCUSED as «Entschuldigt»', () => {
    expect(statusLabel('EXCUSED')).toBe('Entschuldigt')
  })

  it('labels UNEXCUSED as «Unentschuldigt»', () => {
    expect(statusLabel('UNEXCUSED')).toBe('Unentschuldigt')
  })
})

describe('ABSENCE_STATUSES', () => {
  it('lists every status in display order: open, excused, unexcused', () => {
    expect(ABSENCE_STATUSES).toEqual(['OPEN', 'EXCUSED', 'UNEXCUSED'])
  })
})

describe('isAbsenceStatus', () => {
  it('accepts every known status', () => {
    expect(ABSENCE_STATUSES.every((status) => isAbsenceStatus(status))).toBe(true)
  })

  it('rejects unknown values, the empty string, null and undefined', () => {
    expect(isAbsenceStatus('PENDING')).toBe(false)
    expect(isAbsenceStatus('open')).toBe(false)
    expect(isAbsenceStatus('')).toBe(false)
    expect(isAbsenceStatus(null)).toBe(false)
    expect(isAbsenceStatus(undefined)).toBe(false)
  })
})
