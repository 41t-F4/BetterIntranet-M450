import { useId } from 'react'
import { GradeValue } from './GradeValue'
import { SubjectCard } from './SubjectCard'
import { semesterAverage } from './lib/averages'
import type { SubjectGrades } from './types'
import styles from './SemesterSection.module.css'

export interface SemesterSectionProps {
  semester: string
  subjects: readonly SubjectGrades[]
}

/** Section of one semester: heading with the mean of the subject averages and a grid of subject cards. */
export function SemesterSection({ semester, subjects }: SemesterSectionProps) {
  const headingId = useId()
  return (
    <section className={styles.section} aria-labelledby={headingId}>
      <h2 id={headingId} className={styles.heading}>
        {semester} · Ø <GradeValue value={semesterAverage(subjects)} className={styles.average} />
      </h2>
      <div className={styles.grid}>
        {subjects.map((subject) => (
          <SubjectCard key={subject.subject} subject={subject} />
        ))}
      </div>
    </section>
  )
}
