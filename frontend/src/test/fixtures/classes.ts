import type { SchoolClass } from '../../modules/classes-courses/types'

/** `GET /api/classes` response for the default user: one class. */
export const myClassesFixture: SchoolClass[] = [
  {
    id: 7471,
    shortName: 'AP 24b',
    longName: 'Informatiker/innen Applikationsentwicklung',
    department: 'IT',
    classTeacher: 'MID',
    schoolYear: '2026/27',
    role: 'STUDENT',
  },
]
