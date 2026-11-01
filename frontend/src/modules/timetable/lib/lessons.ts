import type { Holiday, IsoDate, Lesson } from '../types'

export interface TimeOfDay {
  hours: number
  minutes: number
}

/** Parses `HH:mm:ss` or `HH:mm` (both are sent by the backend). Throws on any other input. */
export function parseTime(_value: string): TimeOfDay {
  throw new Error('not implemented')
}

/** Formats a backend time as `HH:mm`. */
export function formatTime(_value: string): string {
  throw new Error('not implemented')
}

/**
 * Groups lessons by day in the order of `days`, each day sorted by start time. Days without
 * lessons map to an empty list; lessons outside `days` are dropped.
 */
export function groupLessonsByDay(_lessons: readonly Lesson[], _days: readonly IsoDate[]): Map<IsoDate, Lesson[]> {
  throw new Error('not implemented')
}

/** The holiday covering the given date (bounds inclusive), if any. */
export function holidayFor(_date: IsoDate, _holidays: readonly Holiday[]): Holiday | undefined {
  throw new Error('not implemented')
}
