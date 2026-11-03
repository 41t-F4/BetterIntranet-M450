import { useId, useState } from 'react'
import { Card } from '../../shared/ui/Card'
import { AveragesChart } from './AveragesChart'
import { GradeValue } from './GradeValue'
import { chartData, semesters, subjectsOfSemester, type AveragesChartDatum } from './lib/averages'
import type { GradesOverview } from './types'
import styles from './AveragesCard.module.css'
import table from './GradesTable.module.css'

export interface AveragesCardProps {
  overview: GradesOverview
}

/**
 * Top card «Notendurchschnitte»: overall average, semester selector, bar chart and summary table
 * with one entry per subject of the selected semester (newest semester by default).
 */
export function AveragesCard({ overview }: AveragesCardProps) {
  const available = semesters(overview)
  const [selected, setSelected] = useState(available[0])
  const selectId = useId()

  return (
    <Card className={styles.card}>
      <header className={styles.header}>
        <h1 className={styles.title}>Notendurchschnitte</h1>
        <p className={styles.overall}>
          Gesamt <GradeValue value={overview.overallAverage} className={styles.overallValue} />
        </p>
      </header>

      {selected === undefined ? (
        <p className={styles.empty}>Noch keine Noten vorhanden.</p>
      ) : (
        <>
          <div className={styles.filter}>
            <label htmlFor={selectId} className={styles.filterLabel}>
              Semester
            </label>
            <select
              id={selectId}
              value={selected}
              onChange={(event) => setSelected(event.target.value)}
            >
              {available.map((semester) => (
                <option key={semester} value={semester}>
                  {semester}
                </option>
              ))}
            </select>
          </div>
          <SemesterAverages data={chartData(subjectsOfSemester(overview, selected))} />
        </>
      )}
    </Card>
  )
}

/** Chart and summary table of the subjects of one semester. */
function SemesterAverages({ data }: { data: AveragesChartDatum[] }) {
  return (
    <>
      <AveragesChart data={data} />
      <div className={table.wrapper}>
        <table className={table.table} aria-label="Durchschnitte pro Fach">
          <thead>
            <tr>
              <th scope="col">Fach</th>
              <th scope="col" className={table.numeric}>
                Durchschnitt
              </th>
              <th scope="col" className={table.numeric}>
                Zeugnis
              </th>
            </tr>
          </thead>
          <tbody>
            {data.map((entry) => (
              <tr key={entry.subject}>
                <th scope="row">
                  {entry.label === entry.subject ? entry.label : <abbr title={entry.subject}>{entry.label}</abbr>}
                </th>
                <td className={table.numeric}>
                  <GradeValue value={entry.average} />
                </td>
                <td className={table.numeric}>
                  <GradeValue value={entry.reportGrade} />
                </td>
              </tr>
            ))}
          </tbody>
        </table>
      </div>
    </>
  )
}
