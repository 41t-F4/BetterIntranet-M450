import type { Absence, AbsenceStatus, AbsencesResponse, AbsencesSummary } from '../../modules/absences/types'

/**
 * Seven absences of the default user (class AP 24b, TBZ, school day Tuesday) in HS 2026, newest first:
 * 2 open, 3 excused, 2 unexcused, 16 lessons in total.
 */
export const absencesFixture: Absence[] = [
  {
    id: 1,
    date: '2026-10-27',
    lessonStart: '08:10:00',
    lessonEnd: '09:45:00',
    subject: 'Englisch',
    teacher: 'MID',
    status: 'OPEN',
    reason: null,
    reportedAt: '2026-10-27T10:00:00',
  },
  {
    id: 2,
    date: '2026-10-20',
    lessonStart: '13:25:00',
    lessonEnd: '15:00:00',
    subject: '450 Applikationen testen',
    teacher: 'VAJ',
    status: 'OPEN',
    reason: null,
    reportedAt: '2026-10-20T15:10:00',
  },
  {
    id: 3,
    date: '2026-09-29',
    lessonStart: '10:05:00',
    lessonEnd: '12:30:00',
    subject: 'Allgemeinbildung Gesellschaft',
    teacher: 'RIA',
    status: 'EXCUSED',
    reason: 'Arzttermin',
    reportedAt: '2026-09-29T12:40:00',
  },
  {
    id: 4,
    date: '2026-09-22',
    lessonStart: '15:20:00',
    lessonEnd: '16:55:00',
    subject: '450 Applikationen testen',
    teacher: 'VAJ',
    status: 'EXCUSED',
    reason: 'Krankheit',
    reportedAt: '2026-09-22T17:00:00',
  },
  {
    id: 5,
    date: '2026-09-08',
    lessonStart: '15:20:00',
    lessonEnd: '16:55:00',
    subject: '450 Applikationen testen',
    teacher: 'VAJ',
    status: 'EXCUSED',
    reason: 'Militär',
    reportedAt: '2026-09-08T17:05:00',
  },
  {
    id: 6,
    date: '2026-09-01',
    lessonStart: '10:05:00',
    lessonEnd: '12:30:00',
    subject: 'Allgemeinbildung Gesellschaft',
    teacher: 'RIA',
    status: 'UNEXCUSED',
    reason: null,
    reportedAt: '2026-09-01T12:45:00',
  },
  {
    id: 7,
    date: '2026-08-18',
    lessonStart: '13:25:00',
    lessonEnd: '15:00:00',
    subject: '450 Applikationen testen',
    teacher: 'VAJ',
    status: 'UNEXCUSED',
    reason: null,
    reportedAt: '2026-08-18T15:05:00',
  },
]

export const absencesSummaryFixture: AbsencesSummary = {
  total: 7,
  open: 2,
  excused: 3,
  unexcused: 2,
  totalLessons: 16,
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
