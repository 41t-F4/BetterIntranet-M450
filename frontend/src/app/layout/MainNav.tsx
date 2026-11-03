import { NavLink } from 'react-router-dom'
import type { NavItem } from '../modules/types'
import styles from './MainNav.module.css'

export interface MainNavProps {
  /** Items of enabled modules, already sorted (see `ModuleRegistry.getNavItems`). */
  items: readonly NavItem[]
}

/** Main navigation below the top bar; collapses to a horizontally scrollable row on small screens. */
export function MainNav({ items }: MainNavProps) {
  return (
    <nav className={styles.nav} aria-label="Hauptnavigation">
      <ul className={styles.list}>
        {items.map((item) => (
          <li key={item.path}>
            <NavLink
              to={item.path}
              end={item.path === '/'}
              className={({ isActive }) => (isActive ? `${styles.link} ${styles.active}` : styles.link)}
            >
              {item.label}
            </NavLink>
          </li>
        ))}
      </ul>
    </nav>
  )
}
