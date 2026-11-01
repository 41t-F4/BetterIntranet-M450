import type { AbsenceStatus, AbsencesResponse } from './types'

export interface AbsencesState {
  /** Last successfully loaded response; kept while a refetch with another filter is running. */
  data: AbsencesResponse | null
  loading: boolean
  error: Error | null
}

/** Loads `GET /api/absences`, refetching whenever the status filter changes. */
export function useAbsences(_status?: AbsenceStatus): AbsencesState {
  return { data: null, loading: true, error: null }
}
