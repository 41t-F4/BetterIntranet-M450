import { addDays, formatWeekLabel, weekRange } from './lib/week'
import type { IsoDate } from './types'
import styles from './WeekNavigation.module.css'

export interface WeekNavigationProps {
  /** Monday of the shown week. */
  weekStart: IsoDate
  /** Today's date; «Heute» jumps to its week. */
  today: IsoDate
  /** Called with the Monday of the newly selected week. */
  onChange: (weekStart: IsoDate) => void
}

const DAYS_PER_WEEK = 7

/** Previous / today / next week buttons together with the week label. */
export function WeekNavigation({ weekStart, today, onChange }: WeekNavigationProps) {
  return (
    <div className={styles.navigation} role="group" aria-label="Woche">
      <div className={styles.buttons}>
        <button type="button" className={styles.button} onClick={() => onChange(addDays(weekStart, -DAYS_PER_WEEK))}>
          Vorherige Woche
        </button>
        <button type="button" className={styles.button} onClick={() => onChange(weekRange(today).from)}>
          Heute
        </button>
        <button type="button" className={styles.button} onClick={() => onChange(addDays(weekStart, DAYS_PER_WEEK))}>
          Nächste Woche
        </button>
      </div>
      <p className={styles.label}>{formatWeekLabel(weekStart)}</p>
    </div>
  )
}
