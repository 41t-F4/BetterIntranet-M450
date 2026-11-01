import type { Holiday, Lesson, TimetableClass } from '../../modules/timetable/types'

/** Monday of the fixture week (KW 45 2026). */
export const FIXTURE_WEEK_START = '2026-11-02'
export const FIXTURE_WEEK_END = '2026-11-06'

/** `GET /api/timetable/classes`, sorted by short name like the backend does. */
export const timetableClassesFixture: TimetableClass[] = [
  { id: 7470, shortName: 'AP 24a', longName: 'Informatiker/innen Applikationsentwicklung', department: 'IT' },
  { id: 7471, shortName: 'AP 24b', longName: 'Informatiker/innen Applikationsentwicklung', department: 'IT' },
  { id: 7372, shortName: 'PE 26a', longName: 'Informatiker/innen Plattformentwicklung', department: 'IT' },
]

/**
 * Lessons of AP 24b in KW 45 2026, mirroring the real TBZ week: Tuesday is the school day with Englisch,
 * Allgemeinbildung Gesellschaft and two blocks of module 450. Allgemeinbildung is cancelled, the second
 * 450 block moved to another room (changed), and an Englisch lesson on the holiday Thursday must not be shown.
 */
export const lessonsFixture: Lesson[] = [
  {
    date: '2026-11-03',
    start: '08:10:00',
    end: '09:45:00',
    subject: 'ENG',
    subjectName: 'Englisch',
    teacher: 'MID',
    room: 'AU-512',
    status: 'REGULAR',
    info: 'oB',
  },
  {
    date: '2026-11-03',
    start: '10:05',
    end: '12:30',
    subject: 'GES',
    subjectName: 'Allgemeinbildung Gesellschaft',
    teacher: 'RIA',
    room: 'AU-202',
    status: 'CANCELLED',
    info: 'Lehrperson abwesend',
  },
  {
    date: '2026-11-03',
    start: '13:25:00',
    end: '15:00:00',
    subject: 'M450',
    subjectName: '450 Applikationen testen',
    teacher: 'VAJ',
    room: 'AU-060',
    status: 'REGULAR',
    info: null,
  },
  {
    date: '2026-11-03',
    start: '15:20:00',
    end: '16:55:00',
    subject: 'M450',
    subjectName: '450 Applikationen testen',
    teacher: 'VAJ',
    room: 'AU-057',
    status: 'CHANGED',
    info: 'Raumänderung',
  },
  {
    date: '2026-11-05',
    start: '08:10:00',
    end: '09:45:00',
    subject: 'ENG',
    subjectName: 'Englisch',
    teacher: 'MID',
    room: 'AU-512',
    status: 'REGULAR',
    info: null,
  },
]

/** `GET /api/timetable/holidays`: the autumn holidays and a one-day holiday on the fixture Thursday. */
export const holidaysFixture: Holiday[] = [
  { name: 'Herbstferien', startDate: '2026-10-05', endDate: '2026-10-18' },
  { name: 'Tag der Schule', startDate: '2026-11-05', endDate: '2026-11-05' },
  { name: 'Weihnachtsferien', startDate: '2026-12-21', endDate: '2027-01-03' },
]
