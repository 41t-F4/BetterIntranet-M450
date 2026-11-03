import { apiGet } from '../../shared/api/client'
import type { Dashboard } from './types'

/** Base path of the endpoint in the backend. */
export const DASHBOARD_API = '/api/dashboard'

/** Loads the dashboard content (news, school events, prevention box, links). Throws `ApiError` on failure. */
export function fetchDashboard(): Promise<Dashboard> {
  return apiGet<Dashboard>(DASHBOARD_API)
}
