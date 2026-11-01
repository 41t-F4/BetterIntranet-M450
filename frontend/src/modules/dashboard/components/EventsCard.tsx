import { Card } from '../../../shared/ui/Card'
import { eventKindLabel } from '../lib/eventKindLabel'
import { formatDateRange } from '../lib/formatDate'
import type { EventKind, SchoolEvent } from '../types'
import shared from './DashboardCard.module.css'
import styles from './EventsCard.module.css'

export interface EventsCardProps {
  events: readonly SchoolEvent[]
}

const BADGE_CLASS: Record<EventKind, string> = {
  HOLIDAY: styles.badgeHoliday,
  EVENT: styles.badgeEvent,
}

/** «Termine der Schule»: upcoming school events and holidays (order as delivered by the backend). */
export function EventsCard({ events }: EventsCardProps) {
  return (
    <Card title="Termine der Schule" className={shared.card}>
      {events.length === 0 ? (
        <p className={shared.empty}>Keine Kalendereinträge gefunden.</p>
      ) : (
        <ul className={shared.list}>
          {events.map((event) => (
            <li key={`${event.startDate}-${event.title}`} className={styles.item}>
              <span className={`${shared.date} ${styles.date}`}>{formatDateRange(event.startDate, event.endDate)}</span>
              <span className={styles.title}>{event.title}</span>
              <span className={`${styles.badge} ${BADGE_CLASS[event.kind]}`}>{eventKindLabel(event.kind)}</span>
            </li>
          ))}
        </ul>
      )}
    </Card>
  )
}
