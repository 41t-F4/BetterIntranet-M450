import type { GradesOverview } from '../../modules/grades/types'

/**
 * `GET /api/grades` response with two semesters (HS 2026, FS 2026), seven subjects and seventeen
 * grades, sorted newest semester first, then subject A–Z. Contains one insufficient grade (3.5),
 * one subject average of exactly 4.0 and one subject without any grades (`average: null`).
 */
export const gradesOverviewFixture: GradesOverview = {
  overallAverage: 5.02,
  subjects: [
    {
      subject: '450 Applikationen testen',
      semester: 'HS 2026',
      average: 5.0,
      reportGrade: 5.0,
      grades: [
        { id: 9, title: 'LB2 Projektarbeit', date: '2026-10-27', grade: 5.0, weight: 2.0 },
        { id: 8, title: 'LB1 Testkonzept', date: '2026-10-05', grade: 4.5, weight: 1.0 },
        { id: 7, title: 'Kurztest Testarten', date: '2026-09-15', grade: 5.5, weight: 1.0 },
      ],
    },
    {
      subject: 'Allgemeinbildung Gesellschaft',
      semester: 'HS 2026',
      average: 4.0,
      reportGrade: 4.0,
      grades: [
        { id: 6, title: 'Prüfung Recht', date: '2026-10-26', grade: 3.5, weight: 1.0 },
        { id: 5, title: 'Prüfung Politik', date: '2026-10-02', grade: 4.5, weight: 1.0 },
        { id: 4, title: 'Prüfung Wirtschaft', date: '2026-09-11', grade: 4.0, weight: 1.0 },
      ],
    },
    {
      subject: 'Englisch',
      semester: 'HS 2026',
      average: 5.88,
      reportGrade: 6.0,
      grades: [
        { id: 3, title: 'Essay', date: '2026-10-21', grade: 6.0, weight: 2.0 },
        { id: 2, title: 'Presentation', date: '2026-09-30', grade: 6.0, weight: 1.0 },
        { id: 1, title: 'Vocabulary Test', date: '2026-09-09', grade: 5.5, weight: 1.0 },
      ],
    },
    {
      subject: '320 Objektorientiert programmieren',
      semester: 'FS 2026',
      average: null,
      reportGrade: null,
      grades: [],
    },
    {
      subject: '322 Benutzerschnittstellen entwerfen und implementieren',
      semester: 'FS 2026',
      average: 5.33,
      reportGrade: 5.5,
      grades: [
        { id: 15, title: 'Kompetenznachweis', date: '2026-06-18', grade: 5.5, weight: 1.0 },
        { id: 14, title: 'Prototyp', date: '2026-05-27', grade: 5.0, weight: 1.0 },
        { id: 13, title: 'Usability-Test', date: '2026-05-06', grade: 5.5, weight: 1.0 },
      ],
    },
    {
      subject: 'Allgemeinbildung Gesellschaft',
      semester: 'FS 2026',
      average: 4.75,
      reportGrade: 5.0,
      grades: [
        { id: 12, title: 'Vertiefungsarbeit', date: '2026-06-10', grade: 5.0, weight: 1.0 },
        { id: 11, title: 'Prüfung Kultur', date: '2026-04-15', grade: 4.5, weight: 1.0 },
      ],
    },
    {
      subject: 'Englisch',
      semester: 'FS 2026',
      average: 5.17,
      reportGrade: 5.0,
      grades: [
        { id: 17, title: 'Reading Comprehension', date: '2026-06-03', grade: 5.0, weight: 2.0 },
        { id: 16, title: 'Listening Test', date: '2026-03-25', grade: 5.5, weight: 1.0 },
      ],
    },
  ],
}

/** `GET /api/grades` response of a user without any grades. */
export const emptyGradesOverviewFixture: GradesOverview = { overallAverage: null, subjects: [] }
