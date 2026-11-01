import type { Holiday, IsoDate, Lesson, TimetableClass } from './types'

/** `GET /api/timetable/classes`: all classes, sorted by short name. */
export function fetchTimetableClasses(): Promise<TimetableClass[]> {
  throw new Error('not implemented')
}

/** `GET /api/timetable?classId=…&from=…&to=…`: lessons of a class in the given (inclusive) date range. */
export function fetchLessons(_classId: number, _from: IsoDate, _to: IsoDate): Promise<Lesson[]> {
  throw new Error('not implemented')
}

/** `GET /api/timetable/holidays`: all holidays, sorted by start date. */
export function fetchHolidays(): Promise<Holiday[]> {
  throw new Error('not implemented')
}
