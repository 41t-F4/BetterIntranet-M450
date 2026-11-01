import { vi } from 'vitest'
import { FIXTURE_WEEK_START } from './fixtures/timetable'

const [year, month, day] = FIXTURE_WEEK_START.split('-').map(Number)

/** Wednesday 10:00 of the fixture week: the «now» of every date-dependent test. */
export const FIXTURE_NOW = new Date(year, month - 1, day + 2, 10, 0)

/**
 * Freezes `Date` at `FIXTURE_NOW` so date-dependent components («current week», «Heute») behave
 * the same on every day the tests run. Timers stay real, so Testing Library's `waitFor` keeps working.
 */
export function freezeClock(now: Date = FIXTURE_NOW): void {
  vi.useFakeTimers({ toFake: ['Date'] })
  vi.setSystemTime(now)
}

export function releaseClock(): void {
  vi.useRealTimers()
}
