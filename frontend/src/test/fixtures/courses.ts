import type { Course } from '../../modules/classes-courses/types'

/**
 * `GET /api/courses` response for the default user: seven courses across all three school parts
 * and two semesters, sorted by school part and code like the backend does.
 */
export const myCoursesFixture: Course[] = [
  {
    id: 'BM-D',
    code: 'D',
    name: 'Deutsch',
    teacher: 'WEB',
    semester: 'HS 2026',
    lessonsPerWeek: 3,
    room: 'AU-305',
    schoolPart: 'BMS',
  },
  {
    id: 'BM-E',
    code: 'E',
    name: 'Englisch',
    teacher: 'KEL',
    semester: 'FS 2026',
    lessonsPerWeek: 2,
    room: 'AU-306',
    schoolPart: 'BMS',
  },
  {
    id: 'BM-M',
    code: 'M',
    name: 'Mathematik',
    teacher: 'HUB',
    semester: 'HS 2026',
    lessonsPerWeek: 4,
    room: 'AU-301',
    schoolPart: 'BMS',
  },
  {
    id: 'TBZ-117',
    code: '117',
    name: 'Informatik- und Netzinfrastruktur',
    teacher: 'CAL',
    semester: 'HS 2026',
    lessonsPerWeek: 4,
    room: 'AU-U60',
    schoolPart: 'TBZ',
  },
  {
    id: 'TBZ-231',
    code: '231',
    name: 'Datenschutz und Datensicherheit',
    teacher: 'MID',
    semester: 'FS 2026',
    lessonsPerWeek: 2,
    room: 'AU-210',
    schoolPart: 'TBZ',
  },
  {
    id: 'UEK-210',
    code: '210',
    name: 'Public Cloud für Anwendungen nutzen',
    teacher: 'ZLI',
    semester: 'HS 2026',
    lessonsPerWeek: 8,
    room: 'ZLI-1',
    schoolPart: 'UEK',
  },
  {
    id: 'UEK-335',
    code: '335',
    name: 'Mobile-Applikation realisieren',
    teacher: 'ZLI',
    semester: 'FS 2026',
    lessonsPerWeek: 8,
    room: 'ZLI-2',
    schoolPart: 'UEK',
  },
]
