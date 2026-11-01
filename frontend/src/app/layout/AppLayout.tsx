import { Outlet } from 'react-router-dom'
import { Card } from '../../shared/ui/Card'
import type { ModuleRegistry } from '../modules/registry'
import { useAppData } from '../providers/useAppData'
import styles from './AppLayout.module.css'
import { Breadcrumb } from './Breadcrumb'
import { MainNav } from './MainNav'
import { TopBar } from './TopBar'

export interface AppLayoutProps {
  registry: ModuleRegistry
}

/** Page frame: top bar, main navigation, breadcrumb and the content container with the routed page. */
export function AppLayout({ registry }: AppLayoutProps) {
  const { status, user, modules, error } = useAppData()

  return (
    <div className={styles.page}>
      <TopBar user={user} />
      {status === 'ready' && <MainNav items={registry.getNavItems(modules)} />}
      <main className={styles.main}>
        {status === 'loading' && (
          <p className={styles.status} role="status">
            Laden …
          </p>
        )}
        {status === 'error' && (
          <Card title="Die Anwendung konnte nicht geladen werden." headingLevel={1} role="alert">
            <p>{error?.message}</p>
            {hasDetail(error) && <p>{error.detail}</p>}
          </Card>
        )}
        {status === 'ready' && (
          <>
            <Breadcrumb />
            <div className={styles.content}>
              <Outlet />
            </div>
          </>
        )}
      </main>
    </div>
  )
}

function hasDetail(error: unknown): error is { detail: string } {
  return typeof error === 'object' && error !== null && typeof (error as { detail?: unknown }).detail === 'string'
}
