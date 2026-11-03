import { useEffect, useState } from 'react'
import { fetchDashboard } from './dashboardApi'
import type { Dashboard } from './types'

export type DashboardState =
  | { status: 'loading' }
  | { status: 'ready'; data: Dashboard }
  | { status: 'error'; error: Error }

/** Loads `GET /api/dashboard` once and exposes its loading state. */
export function useDashboard(): DashboardState {
  const [state, setState] = useState<DashboardState>({ status: 'loading' })

  useEffect(() => {
    let cancelled = false
    fetchDashboard()
      .then((data) => {
        if (!cancelled) setState({ status: 'ready', data })
      })
      .catch((reason: unknown) => {
        if (!cancelled) setState({ status: 'error', error: toError(reason) })
      })
    return () => {
      cancelled = true
    }
  }, [])

  return state
}

function toError(reason: unknown): Error {
  return reason instanceof Error ? reason : new Error(String(reason))
}
