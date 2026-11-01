import { useId, useMemo, useState } from 'react'
import { Card } from '../../shared/ui/Card'
import { useMyCourses } from './hooks/useMyCourses'
import { groupBySchoolPart, type CourseGroup } from './lib/groupBySchoolPart'
import { courseCountLabel, schoolPartLabel } from './lib/labels'
import { distinctSemesters, filterBySemester } from './lib/semesters'
import { ResourceState } from './ResourceState'
import type { Course } from './types'
import styles from './ClassesCoursesPage.module.css'

const TITLE = 'Meine Kurse'

/** `<select>` value that stands for «Alle». */
const ALL_SEMESTERS = ''

/** Card «Meine Kurse»: semester filter and the courses grouped by school part. */
export function MyCoursesCard() {
  const state = useMyCourses()
  return (
    <Card title={TITLE} aria-label={TITLE}>
      <ResourceState state={state}>{(courses) => <CoursesContent courses={courses} />}</ResourceState>
    </Card>
  )
}

function CoursesContent({ courses }: { courses: Course[] }) {
  const [semester, setSemester] = useState(ALL_SEMESTERS)
  const selectId = useId()
  const semesters = useMemo(() => distinctSemesters(courses), [courses])
  const groups = useMemo(
    () => groupBySchoolPart(filterBySemester(courses, semester === ALL_SEMESTERS ? null : semester)),
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
          className={styles.select}
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
      </div>
      {groups.map((group) => (
        <CourseGroupSection key={group.schoolPart} group={group} />
      ))}
    </>
  )
}

function CourseGroupSection({ group }: { group: CourseGroup }) {
  const headingId = useId()
  return (
    <section className={styles.group} aria-labelledby={headingId}>
      <div className={styles.groupHeader}>
        <h3 id={headingId} className={styles.groupTitle}>
          {schoolPartLabel(group.schoolPart)}
        </h3>
        <span className={styles.badge}>{courseCountLabel(group.courses.length)}</span>
      </div>
      <div className={styles.tableWrap}>
        <table className={styles.table}>
          <thead>
            <tr>
              <th scope="col">Code</th>
              <th scope="col">Kurs</th>
              <th scope="col">Lehrperson</th>
              <th scope="col">Semester</th>
              <th scope="col" className={styles.numeric}>
                Lektionen/Woche
              </th>
              <th scope="col">Raum</th>
            </tr>
          </thead>
          <tbody>
            {group.courses.map((course) => (
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
    </section>
  )
}
