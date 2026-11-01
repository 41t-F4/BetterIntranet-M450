import { Card } from '../../shared/ui/Card'
import { GradeValue } from './GradeValue'
import { formatDate, formatGrade } from './lib/format'
import type { SubjectGrades } from './types'
import styles from './SubjectCard.module.css'

export interface SubjectCardProps {
  subject: SubjectGrades
}

/** Card of one subject and semester: title, average, report grade badge and the grade table. */
export function SubjectCard({ subject }: SubjectCardProps) {
  return (
    <Card className={styles.card} aria-label={`${subject.subject} ${subject.semester}`}>
      <header className={styles.header}>
        <div>
          <h3 className={styles.title}>{subject.subject}</h3>
          <p className={styles.semester}>{subject.semester}</p>
        </div>
        <div className={styles.summary}>
          <GradeValue value={subject.average} className={styles.average} />
          {subject.reportGrade !== null && (
            <span className={styles.badge}>Zeugnis {formatGrade(subject.reportGrade)}</span>
          )}
        </div>
      </header>

      {subject.grades.length === 0 ? (
        <p className={styles.empty}>Noch keine Noten.</p>
      ) : (
        <div className={styles.tableWrapper}>
          <table className={styles.table}>
            <thead>
              <tr>
                <th scope="col">Titel</th>
                <th scope="col">Datum</th>
                <th scope="col" className={styles.numeric}>
                  Note
                </th>
                <th scope="col" className={styles.numeric}>
                  Gewichtung
                </th>
              </tr>
            </thead>
            <tbody>
              {subject.grades.map((grade) => (
                <tr key={grade.id}>
                  <td>{grade.title}</td>
                  <td className={styles.date}>{formatDate(grade.date)}</td>
                  <td className={styles.numeric}>
                    <GradeValue value={grade.grade} />
                  </td>
                  <td className={styles.numeric}>{formatGrade(grade.weight)}</td>
                </tr>
              ))}
            </tbody>
          </table>
        </div>
      )}
    </Card>
  )
}
