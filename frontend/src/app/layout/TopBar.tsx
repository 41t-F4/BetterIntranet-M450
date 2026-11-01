import { Link } from 'react-router-dom'
import type { CurrentUser } from '../../shared/api/types'
import styles from './TopBar.module.css'

export interface TopBarProps {
  /** Logged-in user, or `null` while loading / when unavailable. */
  user: CurrentUser | null
}

/** Dark top bar: product name, TBZ wordmark and the logged-in user with a (disabled) logout button. */
export function TopBar({ user }: TopBarProps) {
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
        <div className={styles.user}>
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
