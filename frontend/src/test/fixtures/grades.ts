import type { GradesOverview } from '../../modules/grades/types'

/**
 * `GET /api/grades` response with three school parts, five subjects and twelve grades.
 * Contains one insufficient grade (3.5), one subject average of exactly 4.0 and one subject
 * without any grades (`average: null`).
 */
export const gradesOverviewFixture: GradesOverview = {
  overallAverage: 5.09,
  schoolParts: [
    {
      schoolPart: 'BMS',
      label: 'BMS',
      average: 4.94,
      subjects: [
        {
          subject: 'Deutsch',
          semester: 'HS 2026',
          average: 5.88,
          reportGrade: 6.0,
          grades: [
            { id: 3, title: 'Aufsatz Erörterung', date: '2026-10-21', grade: 6.0, weight: 2.0 },
            { id: 2, title: 'Vortrag Literatur', date: '2026-09-30', grade: 6.0, weight: 1.0 },
            { id: 1, title: 'Grammatiktest', date: '2026-09-09', grade: 5.5, weight: 1.0 },
          ],
        },
        {
          subject: 'Mathematik',
          semester: 'HS 2026',
          average: 4.0,
          reportGrade: 4.0,
          grades: [
            { id: 6, title: 'Prüfung Vektoren', date: '2026-10-26', grade: 3.5, weight: 1.0 },
            { id: 5, title: 'Prüfung Funktionen', date: '2026-10-02', grade: 4.5, weight: 1.0 },
            { id: 4, title: 'Prüfung Algebra', date: '2026-09-11', grade: 4.0, weight: 1.0 },
          ],
        },
      ],
    },
    {
      schoolPart: 'TBZ',
      label: 'TBZ',
      average: 5.0,
      subjects: [
        {
          subject: 'Modul 320 Objektorientiert programmieren',
          semester: 'HS 2026',
          average: null,
          reportGrade: null,
          grades: [],
        },
        {
          subject: 'Modul 450 Applikationen testen',
          semester: 'HS 2026',
          average: 5.0,
          reportGrade: 5.0,
          grades: [
            { id: 9, title: 'LB2 Projektarbeit', date: '2026-10-28', grade: 5.0, weight: 2.0 },
            { id: 8, title: 'LB1 Testkonzept', date: '2026-10-05', grade: 4.5, weight: 1.0 },
            { id: 7, title: 'Kurztest Testarten', date: '2026-09-15', grade: 5.5, weight: 1.0 },
          ],
        },
      ],
    },
    {
      schoolPart: 'UEK',
      label: 'ÜK',
      average: 5.33,
      subjects: [
        {
          subject: 'ÜK 294 Frontend-Applikation',
          semester: 'FS 2026',
          average: 5.33,
          reportGrade: 5.5,
          grades: [
            { id: 12, title: 'Kompetenznachweis', date: '2026-06-18', grade: 5.5, weight: 1.0 },
            { id: 11, title: 'Projektpräsentation', date: '2026-05-27', grade: 5.0, weight: 1.0 },
            { id: 10, title: 'Lernjournal', date: '2026-05-06', grade: 5.5, weight: 1.0 },
          ],
        },
      ],
    },
  ],
}

/** `GET /api/grades` response of a user without any grades. */
export const emptyGradesOverviewFixture: GradesOverview = { overallAverage: null, schoolParts: [] }
