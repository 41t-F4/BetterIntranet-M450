import { ApiError } from '../../shared/api/client'
import { AveragesCard } from './AveragesCard'
import { SemesterSection } from './SemesterSection'
import { semesters, subjectsOfSemester } from './lib/averages'
import { useGrades } from './useGrades'
import styles from './GradesPage.module.css'

/** Page «Noten»: averages card on top, then one section per semester (newest first) with subject cards. */
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
      {semesters(state.overview).map((semester) => (
        <SemesterSection key={semester} semester={semester} subjects={subjectsOfSemester(state.overview, semester)} />
      ))}
    </div>
  )
}

function errorMessage(error: Error): string {
  return error instanceof ApiError && error.detail ? error.detail : error.message
}
