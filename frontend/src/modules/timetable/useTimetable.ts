import { useEffect, useMemo, useState } from 'react'
import { weekRange } from './lib/week'
import { fetchHolidays, fetchLessons, fetchTimetableClasses } from './timetableApi'
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

type Loader<T> = () => Promise<T>

const LOADING = { status: 'loading' } as const

/** Lessons of the given class for the week starting at `weekStart` (a Monday) plus the holidays. */
export function useTimetable(classId: number | undefined, weekStart: IsoDate): ResourceState<TimetableWeek> {
  const load = useMemo<Loader<TimetableWeek> | null>(() => {
    if (classId === undefined) {
      return null
    }
    const { from, to } = weekRange(weekStart)
    return async () => {
      const [lessons, holidays] = await Promise.all([fetchLessons(classId, from, to), fetchHolidays()])
      return { lessons, holidays }
    }
  }, [classId, weekStart])
  return useResource(load)
}

/** All classes selectable in the timetable, loaded once. */
export function useTimetableClasses(): ResourceState<TimetableClass[]> {
  return useResource(fetchTimetableClasses)
}

/**
 * Runs `load` whenever its identity changes and reports the result. While a new loader is pending
 * the state is `loading`; results of superseded loaders are ignored. A `null` loader stays `loading`.
 */
function useResource<T>(load: Loader<T> | null): ResourceState<T> {
  const [result, setResult] = useState<{ load: Loader<T>; state: ResourceState<T> } | null>(null)

  useEffect(() => {
    if (load === null) {
      return
    }
    let cancelled = false
    load()
      .then((data) => {
        if (!cancelled) setResult({ load, state: { status: 'ready', data } })
      })
      .catch((reason: unknown) => {
        if (!cancelled) setResult({ load, state: { status: 'error', error: toError(reason) } })
      })
    return () => {
      cancelled = true
    }
  }, [load])

  return load !== null && result?.load === load ? result.state : LOADING
}

function toError(reason: unknown): Error {
  return reason instanceof Error ? reason : new Error(String(reason))
}
