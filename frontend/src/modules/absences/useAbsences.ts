import { useEffect, useState } from 'react'
import { fetchAbsences } from './absencesApi'
import type { AbsenceStatus, AbsencesResponse } from './types'

export interface AbsencesState {
  /** Last successfully loaded response; kept while a refetch with another filter is running. */
  data: AbsencesResponse | null
  loading: boolean
  error: Error | null
}

/** Outcome of the most recent request that finished, tagged with the filter it was made for. */
interface SettledRequest {
  status: AbsenceStatus | undefined
  data: AbsencesResponse | null
  error: Error | null
}

/** Loads `GET /api/absences`, refetching whenever the status filter changes. */
export function useAbsences(status?: AbsenceStatus): AbsencesState {
  const [settled, setSettled] = useState<SettledRequest | null>(null)

  useEffect(() => {
    let cancelled = false
    fetchAbsences(status)
      .then((data) => {
        if (!cancelled) setSettled({ status, data, error: null })
      })
      .catch((reason: unknown) => {
        if (!cancelled) setSettled((previous) => ({ status, data: previous?.data ?? null, error: toError(reason) }))
      })
    return () => {
      cancelled = true
    }
  }, [status])

  // A request is in flight until a result for the currently requested filter has arrived.
  const loading = settled === null || settled.status !== status
  return {
    data: settled?.data ?? null,
    loading,
    error: loading ? null : settled.error,
  }
}

function toError(reason: unknown): Error {
  return reason instanceof Error ? reason : new Error(String(reason))
}
