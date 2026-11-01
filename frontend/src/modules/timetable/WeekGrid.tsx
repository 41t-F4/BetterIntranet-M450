import { Card } from '../../shared/ui/Card'
import { LessonBlock } from './LessonBlock'
import { groupLessonsByDay, holidayFor } from './lib/lessons'
import { formatDayLabel } from './lib/week'
import type { Holiday, IsoDate, Lesson } from './types'
import styles from './WeekGrid.module.css'

export interface WeekGridProps {
  /** Monday to Friday. */
  days: readonly IsoDate[]
  lessons: readonly Lesson[]
  holidays: readonly Holiday[]
  /** Today's date; its column is highlighted. */
  today: IsoDate
}

/** Five day columns (stacked on small screens) with the lessons of the week; holidays replace a whole column. */
export function WeekGrid({ days, lessons, holidays, today }: WeekGridProps) {
  const lessonsByDay = groupLessonsByDay(lessons, days)
  const columns = days.map((day) => ({
    day,
    holiday: holidayFor(day, holidays),
    lessons: lessonsByDay.get(day) ?? [],
  }))
  const hasLessons = columns.some((column) => column.holiday === undefined && column.lessons.length > 0)

  return (
    <Card>
      {!hasLessons && <p className={styles.empty}>Keine Lektionen in dieser Woche.</p>}
      <div className={styles.grid}>
        {columns.map(({ day, holiday, lessons: dayLessons }) => (
          <DayColumn key={day} day={day} holiday={holiday} lessons={dayLessons} isToday={day === today} />
        ))}
      </div>
    </Card>
  )
}

interface DayColumnProps {
  day: IsoDate
  holiday: Holiday | undefined
  lessons: readonly Lesson[]
  isToday: boolean
}

function DayColumn({ day, holiday, lessons, isToday }: DayColumnProps) {
  const headingId = `timetable-day-${day}`
  return (
    <section
      className={[styles.day, holiday && styles.dayHoliday].filter(Boolean).join(' ')}
      aria-labelledby={headingId}
      aria-current={isToday ? 'date' : undefined}
    >
      <h3 id={headingId} className={styles.dayTitle}>
        {formatDayLabel(day)}
      </h3>
      {holiday ? (
        <p className={styles.holiday}>{holiday.name}</p>
      ) : lessons.length > 0 ? (
        <ul className={styles.lessons}>
          {lessons.map((lesson) => (
            <LessonBlock key={`${lesson.start}-${lesson.subject}`} lesson={lesson} />
          ))}
        </ul>
      ) : (
        <p className={styles.free}>Kein Unterricht</p>
      )}
    </section>
  )
}
