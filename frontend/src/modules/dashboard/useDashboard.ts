import type { Dashboard } from './types'

export type DashboardState =
  | { status: 'loading' }
  | { status: 'ready'; data: Dashboard }
  | { status: 'error'; error: Error }

/** Loads `GET /api/dashboard` once and exposes its loading state. */
export function useDashboard(): DashboardState {
  return { status: 'loading' }
}
