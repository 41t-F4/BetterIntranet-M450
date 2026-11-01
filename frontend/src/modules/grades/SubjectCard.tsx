import { Card } from '../../shared/ui/Card'
import { GradeValue } from './GradeValue'
import { formatDate, formatGrade } from './lib/format'
import type { SubjectGrades } from './types'
import styles from './SubjectCard.module.css'
import table from './GradesTable.module.css'

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
        <div className={table.wrapper}>
          <table className={table.table}>
            <thead>
              <tr>
                <th scope="col">Titel</th>
                <th scope="col">Datum</th>
                <th scope="col" className={table.numeric}>
                  Note
                </th>
                <th scope="col" className={table.numeric}>
                  Gewichtung
                </th>
              </tr>
            </thead>
            <tbody>
              {subject.grades.map((grade) => (
                <tr key={grade.id}>
                  <td>{grade.title}</td>
                  <td className={table.muted}>{formatDate(grade.date)}</td>
                  <td className={table.numeric}>
                    <GradeValue value={grade.grade} />
                  </td>
                  <td className={table.numeric}>{formatGrade(grade.weight)}</td>
                </tr>
              ))}
            </tbody>
          </table>
        </div>
      )}
    </Card>
  )
}
