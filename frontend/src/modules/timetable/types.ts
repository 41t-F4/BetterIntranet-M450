/** Types of the timetable backend contract (`/api/timetable/**`). */

/** Calendar date as `YYYY-MM-DD` (local date, no time zone). */
export type IsoDate = string

/** One entry of `GET /api/timetable/classes`. */
export interface TimetableClass {
  id: number
  shortName: string
  longName: string
  department: string
}

export type LessonStatus = 'REGULAR' | 'CANCELLED' | 'CHANGED'

/** One entry of `GET /api/timetable?classId=…&from=…&to=…`. */
export interface Lesson {
  date: IsoDate
  /** `HH:mm:ss` or `HH:mm`. */
  start: string
  /** `HH:mm:ss` or `HH:mm`. */
  end: string
  subject: string
  subjectName: string
  teacher: string
  room: string
  status: LessonStatus
  info: string | null
}

/** One entry of `GET /api/timetable/holidays`. Bounds are inclusive. */
export interface Holiday {
  name: string
  startDate: IsoDate
  endDate: IsoDate
}
