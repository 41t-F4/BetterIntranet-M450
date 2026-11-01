import { describe, expect, it } from 'vitest'
import { greeting } from './greeting'

/** Local date on an arbitrary day at the given time. */
function at(hours: number, minutes: number): Date {
  return new Date(2026, 10, 2, hours, minutes)
}

describe('greeting', () => {
  it.each([
    [0, 0, 'Guten Morgen, Nicola'],
    [10, 59, 'Guten Morgen, Nicola'],
    [11, 0, 'Guten Tag, Nicola'],
    [17, 59, 'Guten Tag, Nicola'],
    [18, 0, 'Guten Abend, Nicola'],
    [23, 59, 'Guten Abend, Nicola'],
  ])('greets at %i:%i with «%s»', (hours, minutes, expected) => {
    expect(greeting(at(hours, minutes), 'Nicola')).toBe(expected)
  })

  it('omits the name while the user is not known', () => {
    expect(greeting(at(12, 0), null)).toBe('Guten Tag')
  })

  it('omits the name when it is blank', () => {
    expect(greeting(at(8, 0), '  ')).toBe('Guten Morgen')
  })
})
