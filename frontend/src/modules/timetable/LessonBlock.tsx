import styles from './LessonBlock.module.css'
import { formatTime } from './lib/lessons'
import type { Lesson, LessonStatus } from './types'

export interface LessonBlockProps {
  lesson: Lesson
}

/** Visual treatment per lesson status: extra class on the block and the badge text, if any. */
const STATUS_PRESENTATION: Record<LessonStatus, { className?: string; badge?: string }> = {
  REGULAR: {},
  CANCELLED: { className: styles.cancelled, badge: 'Ausfall' },
  CHANGED: { className: styles.changed, badge: 'Änderung' },
}

/** One lesson in a day column: time range, subject, teacher, room, status and info. */
export function LessonBlock({ lesson }: LessonBlockProps) {
  const { className, badge } = STATUS_PRESENTATION[lesson.status]
  return (
    <li className={[styles.lesson, className].filter(Boolean).join(' ')}>
      <span className={styles.time}>{`${formatTime(lesson.start)} – ${formatTime(lesson.end)}`}</span>
      <span className={styles.subjectLine}>
        <strong className={styles.subject} title={lesson.subjectName}>
          {lesson.subject}
        </strong>
        {badge && <span className={styles.badge}>{badge}</span>}
      </span>
      <span className={styles.meta}>
        <span>{lesson.teachers[0]}</span>
        <span>{lesson.rooms[0]}</span>
      </span>
      {lesson.info && <small className={styles.info}>{lesson.info}</small>}
    </li>
  )
}
