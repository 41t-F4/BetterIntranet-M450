import type { Dashboard } from '../../modules/dashboard/types'

/** `GET /api/dashboard` response matching the backend contract. */
export const dashboardFixture: Dashboard = {
  news: [
    {
      id: 3,
      title: 'Tag der offenen Tür 2026',
      text: 'Am Samstag öffnet die TBZ ihre Türen für Interessierte.',
      publishedAt: '2026-10-28',
    },
    {
      id: 2,
      title: 'Neue Mediothek-Öffnungszeiten',
      text: 'Die Mediothek ist neu bis 18 Uhr geöffnet.',
      publishedAt: '2026-10-14',
    },
  ],
  events: [
    { title: 'Elternabend Abteilung IT', startDate: '2026-11-19', endDate: '2026-11-19', kind: 'EVENT' },
    { title: 'Weihnachtsferien', startDate: '2026-12-21', endDate: '2027-01-02', kind: 'HOLIDAY' },
  ],
  prevention: {
    title: 'Prävention',
    text: '147.ch unterstützt junge Menschen bei Sorgen & Problemen.',
    linkText: 'www.147.ch',
    url: 'https://www.147.ch',
  },
  myLinks: [
    { label: 'Meine Kurse', url: '/klassen-kurse', external: false },
    { label: 'Absenzen: Einträge', url: '/absenzen', external: false },
    { label: 'Noten', url: '/noten', external: false },
  ],
  quickLinks: [{ label: 'Service Desk Pro', url: 'https://servicedesk.tbz.ch', external: true }],
}
