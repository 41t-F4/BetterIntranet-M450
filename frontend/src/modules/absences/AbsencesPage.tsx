import { useId, useState, type ChangeEvent } from 'react'
import { ApiError } from '../../shared/api/client'
import { Card } from '../../shared/ui/Card'
import styles from './AbsencesPage.module.css'
import { formatDate, formatDateTime, formatTimeRange } from './lib/format'
import { ABSENCE_STATUSES, statusLabel } from './lib/status'
import type { Absence, AbsenceStatus, AbsencesSummary } from './types'
import { useAbsences } from './useAbsences'

const OPEN_HINT = 'Offene Absenzen müssen innert 14 Tagen entschuldigt werden.'

/** CSS class that sets `--status-color` for tiles and badges of a status. */
const STATUS_CLASS: Record<AbsenceStatus, string> = {
  OPEN: styles.open,
  EXCUSED: styles.excused,
  UNEXCUSED: styles.unexcused,
}

/** Absences of the logged-in user: summary tiles plus a filterable, read-only list. */
export function AbsencesPage() {
  const [status, setStatus] = useState<AbsenceStatus | undefined>(undefined)
  const { data, loading, error } = useAbsences(status)
  const selectId = useId()

  const onStatusChange = (event: ChangeEvent<HTMLSelectElement>) => {
    setStatus(ABSENCE_STATUSES.find((candidate) => candidate === event.target.value))
  }

  return (
    <div className={styles.page}>
      <Card title="Absenzen" headingLevel={1}>
        <SummaryTiles summary={data?.summary ?? null} />
      </Card>

      <Card title="Einträge">
        <div className={styles.toolbar}>
          <label htmlFor={selectId} className={styles.filterLabel}>
            Status
          </label>
          <select id={selectId} className={styles.select} value={status ?? ''} onChange={onStatusChange}>
            <option value="">Alle</option>
            {ABSENCE_STATUSES.map((option) => (
              <option key={option} value={option}>
                {statusLabel(option)}
              </option>
            ))}
          </select>
        </div>

        {error ? (
          <LoadError error={error} />
        ) : data === null ? (
          <p role="status" className={styles.muted}>
            Absenzen werden geladen …
          </p>
        ) : data.absences.length === 0 ? (
          <p className={styles.muted}>Keine Absenzen vorhanden.</p>
        ) : (
          <AbsencesTable absences={data.absences} busy={loading} />
        )}

        {data !== null && error === null && data.summary.open > 0 && <p className={styles.hint}>{OPEN_HINT}</p>}
      </Card>
    </div>
  )
}

function SummaryTiles({ summary }: { summary: AbsencesSummary | null }) {
  const tiles: { label: string; value: number | undefined; className?: string }[] = [
    { label: 'Total', value: summary?.total },
    { label: statusLabel('OPEN'), value: summary?.open, className: STATUS_CLASS.OPEN },
    { label: statusLabel('EXCUSED'), value: summary?.excused, className: STATUS_CLASS.EXCUSED },
    { label: statusLabel('UNEXCUSED'), value: summary?.unexcused, className: STATUS_CLASS.UNEXCUSED },
    { label: 'Lektionen', value: summary?.totalLessons },
  ]
  return (
    <dl className={styles.tiles}>
      {tiles.map((tile) => (
        <div key={tile.label} className={[styles.tile, tile.className].filter(Boolean).join(' ')}>
          <dt className={styles.tileLabel}>{tile.label}</dt>
          <dd className={styles.tileValue}>{tile.value ?? '–'}</dd>
        </div>
      ))}
    </dl>
  )
}

function AbsencesTable({ absences, busy }: { absences: Absence[]; busy: boolean }) {
  return (
    <div className={styles.tableWrap} aria-busy={busy}>
      <table className={styles.table}>
        <thead>
          <tr>
            <th scope="col">Datum</th>
            <th scope="col">Zeit</th>
            <th scope="col">Fach</th>
            <th scope="col">Lehrperson</th>
            <th scope="col">Status</th>
            <th scope="col">Grund</th>
            <th scope="col">Gemeldet am</th>
          </tr>
        </thead>
        <tbody>
          {absences.map((absence) => (
            <tr key={absence.id}>
              <td className={styles.nowrap}>{formatDate(absence.date)}</td>
              <td className={styles.nowrap}>{formatTimeRange(absence.lessonStart, absence.lessonEnd)}</td>
              <td>{absence.subject}</td>
              <td>{absence.teacher}</td>
              <td>
                <StatusBadge status={absence.status} />
              </td>
              <td>{absence.reason ?? '–'}</td>
              <td className={styles.nowrap}>{formatDateTime(absence.reportedAt)}</td>
            </tr>
          ))}
        </tbody>
      </table>
    </div>
  )
}

function StatusBadge({ status }: { status: AbsenceStatus }) {
  return <span className={`${styles.badge} ${STATUS_CLASS[status]}`}>{statusLabel(status)}</span>
}

function LoadError({ error }: { error: Error }) {
  const detail = error instanceof ApiError ? (error.detail ?? error.title) : error.message
  return (
    <div role="alert" className={styles.alert}>
      <p className={styles.alertTitle}>Absenzen konnten nicht geladen werden.</p>
      <p>{detail}</p>
    </div>
  )
}
