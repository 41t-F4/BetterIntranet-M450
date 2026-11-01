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
 * Lessons of AP 24b in KW 45 2026. Contains a regular week, one cancelled lesson (Tuesday), one
 * changed lesson (Wednesday) and one lesson on a holiday (Thursday) that must not be shown.
 */
export const lessonsFixture: Lesson[] = [
  {
    date: '2026-11-02',
    start: '08:10:00',
    end: '09:45:00',
    subject: '117',
    subjectName: '117 Informatik- und Netzinfrastruktur',
    teacher: 'CAL',
    room: 'AU-U60',
    status: 'REGULAR',
    info: null,
  },
  {
    date: '2026-11-02',
    start: '10:05:00',
    end: '11:40:00',
    subject: 'M450',
    subjectName: '450 Applikationen testen',
    teacher: 'HAE',
    room: 'AU-411',
    status: 'REGULAR',
    info: null,
  },
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
    end: '11:40',
    subject: 'DEU',
    subjectName: 'Deutsch',
    teacher: 'STE',
    room: 'AU-512',
    status: 'CANCELLED',
    info: 'Lehrperson abwesend',
  },
  {
    date: '2026-11-04',
    start: '13:15:00',
    end: '14:50:00',
    subject: 'SPO',
    subjectName: 'Sport',
    teacher: 'BRA',
    room: 'TH-1',
    status: 'CHANGED',
    info: 'Raumänderung',
  },
  {
    date: '2026-11-05',
    start: '08:10:00',
    end: '09:45:00',
    subject: 'MAT',
    subjectName: 'Mathematik',
    teacher: 'KEL',
    room: 'AU-305',
    status: 'REGULAR',
    info: null,
  },
  {
    date: '2026-11-06',
    start: '08:10:00',
    end: '09:45:00',
    subject: 'M320',
    subjectName: '320 Objektorientiert programmieren',
    teacher: 'ZUE',
    room: 'AU-411',
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
