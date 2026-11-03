import { useEffect, useState } from 'react'
import { fetchGrades } from './gradesApi'
import type { GradesOverview } from './types'

export type GradesState =
  | { status: 'loading' }
  | { status: 'ready'; overview: GradesOverview }
  | { status: 'error'; error: Error }

/** Loads `GET /api/grades` once when mounted and exposes the loading state. */
export function useGrades(): GradesState {
  const [state, setState] = useState<GradesState>({ status: 'loading' })

  useEffect(() => {
    let cancelled = false
    fetchGrades()
      .then((overview) => {
        if (!cancelled) setState({ status: 'ready', overview })
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
