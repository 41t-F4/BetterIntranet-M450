import type { Course } from '../../modules/classes-courses/types'

/**
 * `GET /api/courses` response for the default user (class AP 24b, TBZ): the eight courses of the
 * semesters HS 2026 and FS 2026, sorted newest semester first and then by code like the backend does.
 */
export const myCoursesFixture: Course[] = [
  {
    id: '306-HS26',
    code: '306',
    name: 'IT-Kleinprojekt abwickeln',
    teacher: 'MUH',
    semester: 'HS 2026',
    lessonsPerWeek: 4,
    room: 'AU-057',
  },
  {
    id: '450-HS26',
    code: '450',
    name: 'Applikationen testen',
    teacher: 'VAJ',
    semester: 'HS 2026',
    lessonsPerWeek: 4,
    room: 'AU-060',
  },
  {
    id: 'ENG-HS26',
    code: 'ENG',
    name: 'Englisch',
    teacher: 'MID',
    semester: 'HS 2026',
    lessonsPerWeek: 2,
    room: 'AU-512',
  },
  {
    id: 'GES-HS26',
    code: 'GES',
    name: 'Allgemeinbildung Gesellschaft',
    teacher: 'RIA',
    semester: 'HS 2026',
    lessonsPerWeek: 3,
    room: 'AU-202',
  },
  {
    id: '320-FS26',
    code: '320',
    name: 'Objektorientiert programmieren',
    teacher: 'HEA',
    semester: 'FS 2026',
    lessonsPerWeek: 4,
    room: 'AU-060',
  },
  {
    id: '322-FS26',
    code: '322',
    name: 'Benutzerschnittstellen entwerfen und implementieren',
    teacher: 'KAS',
    semester: 'FS 2026',
    lessonsPerWeek: 4,
    room: 'AU-057',
  },
  {
    id: 'ENG-FS26',
    code: 'ENG',
    name: 'Englisch',
    teacher: 'MID',
    semester: 'FS 2026',
    lessonsPerWeek: 2,
    room: 'AU-512',
  },
  {
    id: 'GES-FS26',
    code: 'GES',
    name: 'Allgemeinbildung Gesellschaft',
    teacher: 'RIA',
    semester: 'FS 2026',
    lessonsPerWeek: 3,
    room: 'AU-202',
  },
]
