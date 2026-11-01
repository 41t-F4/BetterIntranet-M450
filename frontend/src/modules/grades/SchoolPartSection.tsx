import { GradeValue } from './GradeValue'
import { SubjectCard } from './SubjectCard'
import type { SchoolPartGrades } from './types'
import styles from './SchoolPartSection.module.css'

export interface SchoolPartSectionProps {
  part: SchoolPartGrades
}

/** Section of one school part (BMS, TBZ, ÜK): heading with average and a grid of subject cards. */
export function SchoolPartSection({ part }: SchoolPartSectionProps) {
  const headingId = `school-part-${part.schoolPart}`
  return (
    <section className={styles.section} aria-labelledby={headingId}>
      <h2 id={headingId} className={styles.heading}>
        {part.label} <GradeValue value={part.average} className={styles.average} />
      </h2>
      <div className={styles.grid}>
        {part.subjects.map((subject) => (
          <SubjectCard key={`${subject.subject}-${subject.semester}`} subject={subject} />
        ))}
      </div>
    </section>
  )
}
