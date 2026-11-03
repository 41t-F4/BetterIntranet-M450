import { MyClassesCard } from './MyClassesCard'
import { MyCoursesCard } from './MyCoursesCard'
import styles from './ClassesCoursesPage.module.css'

/** Page «Klassen & Kurse»: the classes and the courses of the logged-in user, each in its own card. */
export function ClassesCoursesPage() {
  return (
    <div className={styles.page}>
      <h1 className={styles.pageTitle}>Klassen & Kurse</h1>
      <MyClassesCard />
      <MyCoursesCard />
    </div>
  )
}
