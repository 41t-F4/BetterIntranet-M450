import { apiGet } from '../../shared/api/client'
import type { Dashboard } from './types'

/** Loads the dashboard content (news, school events, prevention box, links). Throws `ApiError` on failure. */
export function fetchDashboard(): Promise<Dashboard> {
  return apiGet<Dashboard>('/api/dashboard')
}
