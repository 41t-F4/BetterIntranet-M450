import { describe, expect, it } from 'vitest'
import { ABSENCE_STATUSES, statusLabel } from './status'

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
