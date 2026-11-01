import { ApiError } from '../../shared/api/client'
import { AveragesCard } from './AveragesCard'
import { useGrades } from './useGrades'
import styles from './GradesPage.module.css'

/** Page «Noten»: averages card on top, then one section per school part with subject cards. */
export function GradesPage() {
  const state = useGrades()

  if (state.status === 'loading') {
    return (
      <p role="status" className={styles.status}>
        Noten werden geladen…
      </p>
    )
  }

  if (state.status === 'error') {
    return (
      <div role="alert" className={styles.alert}>
        <strong>Noten konnten nicht geladen werden.</strong> {errorMessage(state.error)}
      </div>
    )
  }

  return (
    <div className={styles.page}>
      <AveragesCard overview={state.overview} />
    </div>
  )
}

function errorMessage(error: Error): string {
  return error instanceof ApiError && error.detail ? error.detail : error.message
}
