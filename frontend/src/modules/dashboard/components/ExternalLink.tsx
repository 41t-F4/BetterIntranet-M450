import type { ReactNode } from 'react'
import shared from './DashboardCard.module.css'

export interface ExternalLinkProps {
  href: string
  children: ReactNode
}

/** Link to another site: opens in a new tab without opener access and announces this to screen readers. */
export function ExternalLink({ href, children }: ExternalLinkProps) {
  return (
    <a href={href} target="_blank" rel="noopener noreferrer">
      {children}{' '}
      <span className={shared.visuallyHidden}>(öffnet in neuem Tab)</span>
    </a>
  )
}
