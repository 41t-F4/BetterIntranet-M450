import { Card } from '../../shared/ui/Card'
import { AveragesChart } from './AveragesChart'
import { GradeValue } from './GradeValue'
import { chartData } from './lib/averages'
import type { GradesOverview } from './types'
import styles from './AveragesCard.module.css'

export interface AveragesCardProps {
  overview: GradesOverview
}

/** Top card «Notendurchschnitte»: overall average, bar chart and a summary table per school part. */
export function AveragesCard({ overview }: AveragesCardProps) {
  const data = chartData(overview)
  const hasGrades = data.length > 0

  return (
    <Card className={styles.card}>
      <header className={styles.header}>
        <h1 className={styles.title}>Notendurchschnitte</h1>
        <p className={styles.overall}>
          Gesamt <GradeValue value={overview.overallAverage} className={styles.overallValue} />
        </p>
      </header>

      {hasGrades ? (
        <>
          <AveragesChart data={data} />
          <div className={styles.tableWrapper}>
            <table className={styles.table} aria-label="Durchschnitte pro Schulteil">
              <thead>
                <tr>
                  <th scope="col">Schulteil</th>
                  <th scope="col" className={styles.numeric}>
                    Durchschnitt
                  </th>
                  <th scope="col" className={styles.numeric}>
                    Zeugnis
                  </th>
                </tr>
              </thead>
              <tbody>
                {data.map((entry) => (
                  <tr key={entry.schoolPart}>
                    <th scope="row">{entry.label}</th>
                    <td className={styles.numeric}>
                      <GradeValue value={entry.average} />
                    </td>
                    <td className={styles.numeric}>
                      <GradeValue value={entry.reportGrade} />
                    </td>
                  </tr>
                ))}
              </tbody>
            </table>
          </div>
        </>
      ) : (
        <p className={styles.empty}>Noch keine Noten vorhanden.</p>
      )}
    </Card>
  )
}
