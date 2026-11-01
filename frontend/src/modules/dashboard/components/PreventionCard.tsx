import { Card } from '../../../shared/ui/Card'
import type { Prevention } from '../types'
import { ExternalLink } from './ExternalLink'
import shared from './DashboardCard.module.css'

export interface PreventionCardProps {
  prevention: Prevention
}

/** «Prävention»: hint box with an external link (147.ch). */
export function PreventionCard({ prevention }: PreventionCardProps) {
  return (
    <Card title={prevention.title} className={shared.card}>
      <p className={shared.body}>{prevention.text}</p>
      <ExternalLink href={prevention.url}>{prevention.linkText}</ExternalLink>
    </Card>
  )
}
