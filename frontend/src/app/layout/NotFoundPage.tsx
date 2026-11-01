import { Link } from 'react-router-dom'
import { Card } from '../../shared/ui/Card'

/** Fallback page for unknown paths. */
export function NotFoundPage() {
  return (
    <Card title="Seite nicht gefunden" headingLevel={1}>
      <p>Die angeforderte Seite existiert nicht.</p>
      <p>
        <Link to="/">Zur Startseite</Link>
      </p>
    </Card>
  )
}
