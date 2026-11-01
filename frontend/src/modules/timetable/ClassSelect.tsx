import styles from './ClassSelect.module.css'
import type { TimetableClass } from './types'
import type { ResourceState } from './useTimetable'

export interface ClassSelectProps {
  classes: ResourceState<TimetableClass[]>
  /** Selected class id; `undefined` while the user is not known yet. */
  value: number | undefined
  /** Label shown for `value` while the class list is not available. */
  fallbackLabel?: string | undefined
  onChange: (classId: number) => void
}

const SELECT_ID = 'timetable-class'

/** Class selector of the timetable; keeps the selected class visible while the class list loads. */
export function ClassSelect({ classes, value, fallbackLabel, onChange }: ClassSelectProps) {
  const options =
    classes.status === 'ready'
      ? classes.data.map((entry) => ({ id: entry.id, label: entry.shortName }))
      : value === undefined
        ? []
        : [{ id: value, label: fallbackLabel ?? `Klasse ${value}` }]

  return (
    <div className={styles.field}>
      <label htmlFor={SELECT_ID} className={styles.label}>
        Klasse
      </label>
      <select
        id={SELECT_ID}
        className={styles.select}
        value={value ?? ''}
        disabled={classes.status !== 'ready'}
        onChange={(event) => onChange(Number(event.target.value))}
      >
        {options.map((option) => (
          <option key={option.id} value={option.id}>
            {option.label}
          </option>
        ))}
      </select>
      {classes.status === 'error' && <p className={styles.hint}>Klassenliste nicht verfügbar.</p>}
    </div>
  )
}
