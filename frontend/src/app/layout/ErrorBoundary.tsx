import { Link, isRouteErrorResponse, useRouteError } from 'react-router-dom'
import { Card } from '../../shared/ui/Card'

/** Route-level error element: shows render errors of a route as an error card instead of a blank page. */
export function ErrorBoundary() {
  const error = useRouteError()

  return (
    <Card title="Es ist ein Fehler aufgetreten." headingLevel={1} role="alert">
      <p>{describe(error)}</p>
      <p>
        <Link to="/">Zur Startseite</Link>
      </p>
    </Card>
  )
}

function describe(error: unknown): string {
  if (isRouteErrorResponse(error)) {
    return `${error.status} ${error.statusText}`
  }
  if (error instanceof Error) {
    return error.message
  }
  return 'Unbekannter Fehler'
}
