import { useCurrentUser } from '../../app/providers/useAppData'
import { ApiError } from '../../shared/api/client'
import { Card } from '../../shared/ui/Card'
import { EventsCard } from './components/EventsCard'
import { LinksCard } from './components/LinksCard'
import { NewsCard } from './components/NewsCard'
import { PreventionCard } from './components/PreventionCard'
import styles from './DashboardPage.module.css'
import { greeting } from './lib/greeting'
import type { Dashboard } from './types'
import { useDashboard } from './useDashboard'

export interface DashboardPageProps {
  /** Clock used for the greeting; injectable for deterministic tests. */
  now?: () => Date
}

const systemClock = () => new Date()

/** Home page: greeting plus newsticker, school events, prevention box and link lists. */
export function DashboardPage({ now = systemClock }: DashboardPageProps) {
  const user = useCurrentUser()
  const state = useDashboard()

  return (
    <div className={styles.page}>
      <h1 className={styles.greeting}>{greeting(now(), user?.firstName)}</h1>
      {state.status === 'loading' && (
        <p role="status" className={styles.status}>
          Dashboard wird geladen …
        </p>
      )}
      {state.status === 'error' && <DashboardError error={state.error} />}
      {state.status === 'ready' && <DashboardGrid dashboard={state.data} />}
    </div>
  )
}

function DashboardGrid({ dashboard }: { dashboard: Dashboard }) {
  return (
    <div className={styles.grid}>
      <div className={styles.news}>
        <NewsCard news={dashboard.news} />
      </div>
      <div className={styles.events}>
        <EventsCard events={dashboard.events} />
      </div>
      <div className={styles.side}>
        <PreventionCard prevention={dashboard.prevention} />
        <LinksCard title="Meine Links" links={dashboard.myLinks} />
        <LinksCard title="Quick Links" links={dashboard.quickLinks} />
      </div>
    </div>
  )
}

function DashboardError({ error }: { error: Error }) {
  const detail = error instanceof ApiError ? error.detail : undefined
  return (
    <Card role="alert" className={styles.error}>
      <p className={styles.errorTitle}>Das Dashboard konnte nicht geladen werden.</p>
      <p>{detail ?? 'Bitte versuche es später erneut.'}</p>
    </Card>
  )
}
