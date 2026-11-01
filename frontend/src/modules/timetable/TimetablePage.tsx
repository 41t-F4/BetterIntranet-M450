import { useState } from 'react'
import { useSearchParams } from 'react-router-dom'
import { useCurrentUser } from '../../app/providers/useAppData'
import { ApiError } from '../../shared/api/client'
import { Card } from '../../shared/ui/Card'
import { ClassSelect } from './ClassSelect'
import { parseIsoDate, toIsoDate, weekRange } from './lib/week'
import styles from './TimetablePage.module.css'
import type { IsoDate } from './types'
import { useTimetable, useTimetableClasses } from './useTimetable'
import { WeekGrid } from './WeekGrid'
import { WeekNavigation } from './WeekNavigation'

const CLASS_PARAM = 'classId'
const WEEK_PARAM = 'week'

/**
 * Week view of the timetable for the selected class and week. Both selections live in the URL
 * search params (`?classId=7471&week=2026-11-02`) so every view is linkable; they default to the
 * class of the logged-in user and the current week.
 */
export function TimetablePage() {
  const user = useCurrentUser()
  const [searchParams, setSearchParams] = useSearchParams()
  const [today] = useState(() => toIsoDate(new Date()))

  const classId = parseClassId(searchParams.get(CLASS_PARAM)) ?? user?.classId
  const week = weekRange(parseWeekParam(searchParams.get(WEEK_PARAM), today))

  const classes = useTimetableClasses()
  const timetable = useTimetable(classId, week.from)

  const select = (changes: { classId?: number; week?: IsoDate }) => {
    const next = new URLSearchParams(searchParams)
    const nextClassId = changes.classId ?? classId
    if (nextClassId !== undefined) {
      next.set(CLASS_PARAM, String(nextClassId))
    }
    next.set(WEEK_PARAM, changes.week ?? week.from)
    setSearchParams(next)
  }

  return (
    <div className={styles.page}>
      <Card title="Stundenplan" headingLevel={1}>
        <div className={styles.controls}>
          <ClassSelect
            classes={classes}
            value={classId}
            fallbackLabel={classId === user?.classId ? user?.className : undefined}
            onChange={(nextClassId) => select({ classId: nextClassId })}
          />
          <WeekNavigation
            weekStart={week.from}
            today={today}
            onChange={(weekStart) => select({ week: weekStart })}
          />
        </div>
      </Card>

      {timetable.status === 'loading' && (
        <p className={styles.status} role="status">
          Laden …
        </p>
      )}
      {timetable.status === 'error' && <TimetableError error={timetable.error} />}
      {timetable.status === 'ready' && (
        <WeekGrid
          days={week.days}
          lessons={timetable.data.lessons}
          holidays={timetable.data.holidays}
          today={today}
        />
      )}
    </div>
  )
}

function TimetableError({ error }: { error: Error }) {
  const upstreamUnavailable = error instanceof ApiError && error.status === 502
  const title = upstreamUnavailable
    ? 'Der Stundenplan ist zurzeit nicht erreichbar.'
    : 'Der Stundenplan konnte nicht geladen werden.'
  const detail = error instanceof ApiError ? error.detail : error.message
  return (
    <Card title={title} role="alert">
      {detail && <p>{detail}</p>}
    </Card>
  )
}

function parseClassId(value: string | null): number | undefined {
  const classId = Number(value)
  return value !== null && Number.isInteger(classId) && classId > 0 ? classId : undefined
}

/** The `week` param, or today when it is missing or not a valid date. Any weekday is accepted. */
function parseWeekParam(value: string | null, today: IsoDate): IsoDate {
  if (value === null) {
    return today
  }
  try {
    return toIsoDate(parseIsoDate(value))
  } catch {
    return today
  }
}
