import { apiGet } from '../../shared/api/client'
import type { Holiday, IsoDate, Lesson, TimetableClass } from './types'

/** Base path of the timetable endpoints in the backend. */
export const TIMETABLE_API = '/api/timetable'

/** `GET /api/timetable/classes`: all classes, sorted by short name. */
export function fetchTimetableClasses(): Promise<TimetableClass[]> {
  return apiGet<TimetableClass[]>(`${TIMETABLE_API}/classes`)
}

/** `GET /api/timetable?classId=…&from=…&to=…`: lessons of a class in the given (inclusive) date range. */
export function fetchLessons(classId: number, from: IsoDate, to: IsoDate): Promise<Lesson[]> {
  return apiGet<Lesson[]>(TIMETABLE_API, { classId, from, to })
}

/** `GET /api/timetable/holidays`: all holidays, sorted by start date. */
export function fetchHolidays(): Promise<Holiday[]> {
  return apiGet<Holiday[]>(`${TIMETABLE_API}/holidays`)
}
