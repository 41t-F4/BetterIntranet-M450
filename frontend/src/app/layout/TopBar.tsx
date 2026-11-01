import type { ReactNode } from 'react'
import { Link } from 'react-router-dom'
import type { CurrentUser } from '../../shared/api/types'
import { ThemeToggle } from '../../shared/theme/ThemeToggle'
import styles from './TopBar.module.css'

export interface TopBarProps {
  /** Logged-in user, or `null` while loading / when unavailable. */
  user: CurrentUser | null
  /** Main navigation, rendered between brand and user. */
  children?: ReactNode
}

/**
 * Header: TBZ wordmark and product name, the main navigation and the logged-in user with a (disabled)
 * logout button. On narrow screens the navigation moves to its own scrollable row.
 */
export function TopBar({ user, children }: TopBarProps) {
  return (
    <header className={styles.topBar}>
      <div className={styles.inner}>
        <div className={styles.brand}>
          <span className={styles.wordmark} aria-label="Technische Berufsschule Zürich">
            TBZ
          </span>
          <Link to="/" className={styles.product}>
            BetterIntranet
          </Link>
        </div>
        {children && <div className={styles.nav}>{children}</div>}
        <div className={styles.user}>
          <ThemeToggle />
          {user && (
            <span className={styles.avatar} aria-hidden="true">
              {initials(user.displayName)}
            </span>
          )}
          {user && (
            <span className={styles.identity}>
              <span className={styles.name}>{user.displayName}</span>
              <span className={styles.className}>{user.className}</span>
            </span>
          )}
          <button
            type="button"
            className={styles.logout}
            disabled
            title="SSO"
            aria-label="Abmelden"
          >
            <svg aria-hidden="true" viewBox="0 0 24 24" width="20" height="20" fill="none" stroke="currentColor" strokeWidth="2" strokeLinecap="round" strokeLinejoin="round">
              <path d="M9 21H5a2 2 0 0 1-2-2V5a2 2 0 0 1 2-2h4" />
              <path d="M16 17l5-5-5-5" />
              <path d="M21 12H9" />
            </svg>
          </button>
        </div>
      </div>
    </header>
  )
}

function initials(displayName: string): string {
  return displayName
    .split(/\s+/)
    .filter(Boolean)
    .slice(0, 2)
    .map((part) => part[0]?.toUpperCase() ?? '')
    .join('')
}
