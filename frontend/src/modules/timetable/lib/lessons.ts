import type { Holiday, IsoDate, Lesson } from '../types'

export interface TimeOfDay {
  hours: number
  minutes: number
}

const TIME_PATTERN = /^(\d{2}):(\d{2})(?::(\d{2}))?$/

/** Parses `HH:mm:ss` or `HH:mm` (both are sent by the backend). Throws on any other input. */
export function parseTime(value: string): TimeOfDay {
  const match = TIME_PATTERN.exec(value)
  const hours = Number(match?.[1])
  const minutes = Number(match?.[2])
  if (!match || hours > 23 || minutes > 59) {
    throw new Error(`Ungültige Uhrzeit: ${value}`)
  }
  return { hours, minutes }
}

/** Formats a backend time as `HH:mm`. */
export function formatTime(value: string): string {
  const { hours, minutes } = parseTime(value)
  return `${String(hours).padStart(2, '0')}:${String(minutes).padStart(2, '0')}`
}

/**
 * Groups lessons by day in the order of `days`, each day sorted by start time. Days without
 * lessons map to an empty list; lessons outside `days` are dropped.
 */
export function groupLessonsByDay(lessons: readonly Lesson[], days: readonly IsoDate[]): Map<IsoDate, Lesson[]> {
  const grouped = new Map<IsoDate, Lesson[]>(days.map((day) => [day, []]))
  for (const lesson of lessons) {
    grouped.get(lesson.date)?.push(lesson)
  }
  for (const dayLessons of grouped.values()) {
    dayLessons.sort((a, b) => minutesOfDay(a.start) - minutesOfDay(b.start))
  }
  return grouped
}

/** The holiday covering the given date (bounds inclusive), if any. */
export function holidayFor(date: IsoDate, holidays: readonly Holiday[]): Holiday | undefined {
  return holidays.find((holiday) => holiday.startDate <= date && date <= holiday.endDate)
}

function minutesOfDay(value: string): number {
  const { hours, minutes } = parseTime(value)
  return hours * 60 + minutes
}
