import { Card } from '../../shared/ui/Card'
import { useMyClasses } from './hooks/useMyClasses'
import { roleLabel } from './lib/labels'
import { ResourceState } from './ResourceState'
import type { SchoolClass } from './types'
import styles from './ClassesCoursesPage.module.css'

const TITLE = 'Meine Klassen'

/** Card «Meine Klassen»: the classes of the logged-in user as a table. */
export function MyClassesCard() {
  const state = useMyClasses()
  return (
    <Card title={TITLE} aria-label={TITLE}>
      <ResourceState state={state}>{(classes) => <ClassesTable classes={classes} />}</ResourceState>
    </Card>
  )
}

function ClassesTable({ classes }: { classes: SchoolClass[] }) {
  if (classes.length === 0) {
    return <p className={styles.muted}>Keine Klassen gefunden.</p>
  }
  return (
    <div className={styles.tableWrap}>
      <table className={styles.table}>
        <thead>
          <tr>
            <th scope="col">Klasse</th>
            <th scope="col">Beruf</th>
            <th scope="col">Abteilung</th>
            <th scope="col">Klassenlehrperson</th>
            <th scope="col">Schuljahr</th>
            <th scope="col">Rolle</th>
          </tr>
        </thead>
        <tbody>
          {classes.map((schoolClass) => (
            <tr key={schoolClass.id}>
              <td>{schoolClass.shortName}</td>
              <td>{schoolClass.longName}</td>
              <td>{schoolClass.department}</td>
              <td>{schoolClass.classTeacher}</td>
              <td>{schoolClass.schoolYear}</td>
              <td>{roleLabel(schoolClass.role)}</td>
            </tr>
          ))}
        </tbody>
      </table>
    </div>
  )
}
