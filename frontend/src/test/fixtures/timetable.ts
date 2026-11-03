import { addDays } from '../../modules/timetable/lib/week'
import type { Holiday, IsoDate, Lesson, TimetableClass } from '../../modules/timetable/types'

/**
 * Monday of the fixture week (KW 45 2026). Every date in the timetable fixtures is derived from it,
 * and tests freeze the clock inside this week (see `test/clock.ts`). Neither the real date nor the
 * real timetable is ever used, so these tests do not age.
 */
export const FIXTURE_WEEK_START: IsoDate = '2026-11-02'
export const FIXTURE_WEEK_END: IsoDate = addDays(FIXTURE_WEEK_START, 4)
export const FIXTURE_TUESDAY: IsoDate = addDays(FIXTURE_WEEK_START, 1)
export const FIXTURE_THURSDAY: IsoDate = addDays(FIXTURE_WEEK_START, 3)
export const NEXT_WEEK_START: IsoDate = addDays(FIXTURE_WEEK_START, 7)
export const NEXT_WEEK_END: IsoDate = addDays(NEXT_WEEK_START, 4)
export const PREVIOUS_WEEK_START: IsoDate = addDays(FIXTURE_WEEK_START, -7)

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
    date: FIXTURE_TUESDAY,
    start: '08:10:00',
    end: '09:45:00',
    subject: 'ENG',
    subjectName: 'Englisch',
    teachers: ['MID'],
    rooms: ['AU-512'],
    status: 'REGULAR',
    info: 'oB',
  },
  {
    date: FIXTURE_TUESDAY,
    start: '10:05',
    end: '12:30',
    subject: 'GES',
    subjectName: 'Allgemeinbildung Gesellschaft',
    teachers: ['RIA', 'LUT'],
    rooms: ['AU-202'],
    status: 'CANCELLED',
    info: 'Lehrperson abwesend',
  },
  {
    date: FIXTURE_TUESDAY,
    start: '13:25:00',
    end: '15:00:00',
    subject: 'M450',
    subjectName: '450 Applikationen testen',
    teachers: ['VAJ'],
    rooms: ['AU-060'],
    status: 'REGULAR',
    info: null,
  },
  {
    date: FIXTURE_TUESDAY,
    start: '15:20:00',
    end: '16:55:00',
    subject: 'M450',
    subjectName: '450 Applikationen testen',
    teachers: ['VAJ'],
    rooms: ['AU-057'],
    status: 'CHANGED',
    info: 'Raumänderung',
  },
  {
    date: FIXTURE_THURSDAY,
    start: '08:10:00',
    end: '09:45:00',
    subject: 'ENG',
    subjectName: 'Englisch',
    teachers: ['MID'],
    rooms: ['AU-512'],
    status: 'REGULAR',
    info: null,
  },
]

/** `GET /api/timetable/holidays`: the autumn holidays and a one-day holiday on the fixture Thursday. */
export const holidaysFixture: Holiday[] = [
  { name: 'Herbstferien', startDate: '2026-10-05', endDate: '2026-10-18' },
  { name: 'Tag der Schule', startDate: FIXTURE_THURSDAY, endDate: FIXTURE_THURSDAY },
  { name: 'Weihnachtsferien', startDate: '2026-12-21', endDate: '2027-01-03' },
]
