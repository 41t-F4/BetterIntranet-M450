import styles from './LessonBlock.module.css'
import { formatTime } from './lib/lessons'
import type { Lesson } from './types'

export interface LessonBlockProps {
  lesson: Lesson
}

const STATUS_CLASS = {
  REGULAR: undefined,
  CANCELLED: styles.cancelled,
  CHANGED: styles.changed,
} as const

/** One lesson in a day column: time range, subject, teacher, room, status and info. */
export function LessonBlock({ lesson }: LessonBlockProps) {
  return (
    <li className={[styles.lesson, STATUS_CLASS[lesson.status]].filter(Boolean).join(' ')}>
      <span className={styles.time}>{`${formatTime(lesson.start)} – ${formatTime(lesson.end)}`}</span>
      <span className={styles.subjectLine}>
        <strong className={styles.subject} title={lesson.subjectName}>
          {lesson.subject}
        </strong>
        {lesson.status === 'CANCELLED' && <span className={styles.badge}>Ausfall</span>}
        {lesson.status === 'CHANGED' && <span className={styles.badge}>Änderung</span>}
      </span>
      <span className={styles.meta}>
        <span>{lesson.teacher}</span>
        <span>{lesson.room}</span>
      </span>
      {lesson.info && <small className={styles.info}>{lesson.info}</small>}
    </li>
  )
}
