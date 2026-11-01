import type { ReactNode } from 'react'
import { ApiError } from '../../shared/api/client'
import type { ModuleResourceState } from './hooks/useModuleResource'
import styles from './ClassesCoursesPage.module.css'

export interface ResourceStateProps<T> {
  state: ModuleResourceState<T>
  /** Renders the loaded data. */
  children: (data: T) => ReactNode
}

const FALLBACK_ERROR = 'Die Daten konnten nicht geladen werden.'

/** Renders the loading, disabled and error states of a module resource, or the data via `children`. */
export function ResourceState<T>({ state, children }: ResourceStateProps<T>) {
  switch (state.status) {
    case 'loading':
      return (
        <p role="status" className={styles.muted}>
          Laden …
        </p>
      )
    case 'disabled':
      return <p className={styles.muted}>Dieses Modul ist zurzeit deaktiviert.</p>
    case 'error':
      return (
        <p role="alert" className={styles.error}>
          {errorMessage(state.error)}
        </p>
      )
    case 'ready':
      return children(state.data)
  }
}

function errorMessage(error: Error): string {
  return error instanceof ApiError && error.detail ? error.detail : FALLBACK_ERROR
}
