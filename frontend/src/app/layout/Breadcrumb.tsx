import { Link, useMatches } from 'react-router-dom'
import type { ModuleRouteHandle } from '../modules/types'
import styles from './Breadcrumb.module.css'

interface Crumb {
  label: string
  path: string
}

const HOME: Crumb = { label: 'Home', path: '/' }

/** Breadcrumb derived from the matched routes (`handle.breadcrumb`), e.g. `Home > Noten`. */
export function Breadcrumb() {
  const matches = useMatches()
  const crumbs: Crumb[] = [
    HOME,
    ...matches.flatMap((match) => {
      const handle = match.handle as ModuleRouteHandle | undefined
      const label = handle?.breadcrumb
      return label && match.pathname !== HOME.path ? [{ label, path: match.pathname }] : []
    }),
  ]
  const last = crumbs.length - 1

  return (
    <nav className={styles.breadcrumb} aria-label="Breadcrumb">
      <ol className={styles.list}>
        {crumbs.map((crumb, index) => (
          <li key={crumb.path} className={styles.item}>
            {index === last ? (
              <span aria-current="page">{crumb.label}</span>
            ) : (
              <Link to={crumb.path}>{crumb.label}</Link>
            )}
          </li>
        ))}
      </ol>
    </nav>
  )
}
