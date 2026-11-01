import type { Holiday, IsoDate, Lesson, TimetableClass } from './types'

/** Loading state of a resource fetched from the backend. */
export type ResourceState<T> =
  | { status: 'loading' }
  | { status: 'ready'; data: T }
  | { status: 'error'; error: Error }

export interface TimetableWeek {
  lessons: Lesson[]
  holidays: Holiday[]
}

/** Lessons of the given class for the week starting at `weekStart` (a Monday) plus the holidays. */
export function useTimetable(_classId: number | undefined, _weekStart: IsoDate): ResourceState<TimetableWeek> {
  throw new Error('not implemented')
}

/** All classes selectable in the timetable, loaded once. */
export function useTimetableClasses(): ResourceState<TimetableClass[]> {
  throw new Error('not implemented')
}
