import { useId, useMemo, useState } from 'react'
import { Card } from '../../shared/ui/Card'
import { useMyCourses } from './hooks/useMyCourses'
import { courseCountLabel } from './lib/labels'
import { distinctSemesters, filterBySemester, groupBySemester, type SemesterGroup } from './lib/semesters'
import { ResourceState } from './ResourceState'
import type { Course } from './types'
import styles from './ClassesCoursesPage.module.css'

const TITLE = 'Meine Kurse'

/** `<select>` value that stands for «Alle». */
const ALL_SEMESTERS = ''

/** Card «Meine Kurse»: semester filter (newest semester preselected) and the courses as table. */
export function MyCoursesCard() {
  const state = useMyCourses()
  return (
    <Card title={TITLE} aria-label={TITLE}>
      <ResourceState state={state}>{(courses) => <CoursesContent courses={courses} />}</ResourceState>
    </Card>
  )
}

function CoursesContent({ courses }: { courses: Course[] }) {
  const semesters = useMemo(() => distinctSemesters(courses), [courses])
  const [semester, setSemester] = useState(() => semesters[0] ?? ALL_SEMESTERS)
  const selectId = useId()
  const visibleCourses = useMemo(
    () => filterBySemester(courses, semester === ALL_SEMESTERS ? null : semester),
    [courses, semester],
  )

  if (courses.length === 0) {
    return <p className={styles.muted}>Keine Kurse gefunden.</p>
  }

  return (
    <>
      <div className={styles.filter}>
        <label htmlFor={selectId}>Semester</label>
        <select
          id={selectId}
          value={semester}
          onChange={(event) => setSemester(event.target.value)}
        >
          <option value={ALL_SEMESTERS}>Alle</option>
          {semesters.map((item) => (
            <option key={item} value={item}>
              {item}
            </option>
          ))}
        </select>
        <span className={styles.badge}>{courseCountLabel(visibleCourses.length)}</span>
      </div>
      {semester === ALL_SEMESTERS ? (
        groupBySemester(visibleCourses).map((group) => <SemesterSection key={group.semester} group={group} />)
      ) : (
        <CoursesTable courses={visibleCourses} />
      )}
    </>
  )
}

function SemesterSection({ group }: { group: SemesterGroup }) {
  const headingId = useId()
  return (
    <section className={styles.group} aria-labelledby={headingId}>
      <h3 id={headingId} className={styles.groupTitle}>
        {group.semester}
      </h3>
      <CoursesTable courses={group.courses} />
    </section>
  )
}

function CoursesTable({ courses }: { courses: readonly Course[] }) {
  return (
    <div className={styles.tableWrap}>
      <table className={styles.table}>
        <thead>
          <tr>
            <th scope="col">Modul/Fach</th>
            <th scope="col">Bezeichnung</th>
            <th scope="col">Lehrperson</th>
            <th scope="col">Semester</th>
            <th scope="col" className={styles.numeric}>
              Lektionen/Woche
            </th>
            <th scope="col">Raum</th>
          </tr>
        </thead>
        <tbody>
          {courses.map((course) => (
            <tr key={course.id}>
              <td>{course.code}</td>
              <td>{course.name}</td>
              <td>{course.teacher}</td>
              <td>{course.semester}</td>
              <td className={styles.numeric}>{course.lessonsPerWeek}</td>
              <td>{course.room}</td>
            </tr>
          ))}
        </tbody>
      </table>
    </div>
  )
}
