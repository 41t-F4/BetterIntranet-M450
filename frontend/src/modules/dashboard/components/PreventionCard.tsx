import { Card } from '../../../shared/ui/Card'
import type { Prevention } from '../types'
import shared from './DashboardCard.module.css'

export interface PreventionCardProps {
  prevention: Prevention
}

/** «Prävention»: hint box with an external link (147.ch). */
export function PreventionCard({ prevention }: PreventionCardProps) {
  return (
    <Card title={prevention.title} className={`${shared.card} ${shared.accentViolet}`}>
      <p>{prevention.text}</p>
      <a href={prevention.url} target="_blank" rel="noopener noreferrer">
        {prevention.linkText}{' '}
        <span className={shared.visuallyHidden}>(öffnet in neuem Tab)</span>
      </a>
    </Card>
  )
}
