import { Link } from 'react-router-dom'
import { Card } from '../../../shared/ui/Card'
import type { DashboardLink } from '../types'
import { ExternalLink } from './ExternalLink'
import shared from './DashboardCard.module.css'
import styles from './LinksCard.module.css'

export interface LinksCardProps {
  title: string
  links: readonly DashboardLink[]
}

/** Link list used for «Meine Links» and «Quick Links». External links open in a new tab. */
export function LinksCard({ title, links }: LinksCardProps) {
  return (
    <Card title={title} className={`${shared.card} ${shared.accentTeal}`}>
      {links.length === 0 ? (
        <p className={shared.empty}>Keine Links vorhanden.</p>
      ) : (
        <ul className={shared.list}>
          {links.map((link) => (
            <li key={link.url} className={styles.item}>
              {link.external ? (
                <ExternalLink href={link.url}>{link.label}</ExternalLink>
              ) : (
                <Link to={link.url}>{link.label}</Link>
              )}
            </li>
          ))}
        </ul>
      )}
    </Card>
  )
}
