import type { Absence, AbsenceStatus, AbsencesResponse, AbsencesSummary } from '../../modules/absences/types'

/** Seven absences of the default user: 2 open, 3 excused, 2 unexcused, 12 lessons in total. */
export const absencesFixture: Absence[] = [
  {
    id: 1,
    date: '2026-10-12',
    lessonStart: '08:10:00',
    lessonEnd: '09:45:00',
    subject: 'Englisch',
    teacher: 'MID',
    status: 'OPEN',
    reason: null,
    reportedAt: '2026-10-12T10:00:00',
  },
  {
    id: 2,
    date: '2026-10-14',
    lessonStart: '10:05:00',
    lessonEnd: '11:40:00',
    subject: 'Mathematik',
    teacher: 'KAE',
    status: 'OPEN',
    reason: null,
    reportedAt: '2026-10-14T12:15:00',
  },
  {
    id: 3,
    date: '2026-09-28',
    lessonStart: '13:15:00',
    lessonEnd: '14:50:00',
    subject: 'Deutsch',
    teacher: 'SCH',
    status: 'EXCUSED',
    reason: 'Arzttermin',
    reportedAt: '2026-09-28T15:00:00',
  },
  {
    id: 4,
    date: '2026-09-21',
    lessonStart: '08:10:00',
    lessonEnd: '09:45:00',
    subject: 'Informatik',
    teacher: 'CAL',
    status: 'EXCUSED',
    reason: 'Krankheit',
    reportedAt: '2026-09-21T09:50:00',
  },
  {
    id: 5,
    date: '2026-09-07',
    lessonStart: '15:00:00',
    lessonEnd: '15:45:00',
    subject: 'Sport',
    teacher: 'BRU',
    status: 'EXCUSED',
    reason: 'Militär',
    reportedAt: '2026-09-07T16:00:00',
  },
  {
    id: 6,
    date: '2026-08-31',
    lessonStart: '10:05:00',
    lessonEnd: '11:40:00',
    subject: 'Geschichte',
    teacher: 'WEB',
    status: 'UNEXCUSED',
    reason: null,
    reportedAt: '2026-08-31T11:45:00',
  },
  {
    id: 7,
    date: '2026-08-24',
    lessonStart: '11:50:00',
    lessonEnd: '12:35:00',
    subject: 'Französisch',
    teacher: 'ROS',
    status: 'UNEXCUSED',
    reason: null,
    reportedAt: '2026-08-24T13:00:00',
  },
]

export const absencesSummaryFixture: AbsencesSummary = {
  total: 7,
  open: 2,
  excused: 3,
  unexcused: 2,
  totalLessons: 12,
}

/** `GET /api/absences` without filter. */
export const absencesResponseFixture: AbsencesResponse = {
  summary: absencesSummaryFixture,
  absences: absencesFixture,
}

/** `GET /api/absences` of a user without any absences. */
export const emptyAbsencesResponse: AbsencesResponse = {
  summary: { total: 0, open: 0, excused: 0, unexcused: 0, totalLessons: 0 },
  absences: [],
}

const KNOWN_STATUSES: readonly string[] = ['OPEN', 'EXCUSED', 'UNEXCUSED']

export function isAbsenceStatus(value: string | null): value is AbsenceStatus {
  return value !== null && KNOWN_STATUSES.includes(value)
}

/**
 * `GET /api/absences?status=...` as the backend answers it: the list is narrowed to the status,
 * the summary still covers all absences.
 */
export function absencesResponseForStatus(status: AbsenceStatus | null): AbsencesResponse {
  return {
    summary: absencesSummaryFixture,
    absences: status === null ? absencesFixture : absencesFixture.filter((absence) => absence.status === status),
  }
}
