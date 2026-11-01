/** Types of the `GET /api/dashboard` contract. */

/** School news entry of the newsticker. */
export interface NewsItem {
  id: number
  title: string
  text: string
  /** ISO date (`YYYY-MM-DD`). */
  publishedAt: string
}

/** Kind of a school calendar entry: holidays come from the timetable source, events from the dashboard module. */
export type EventKind = 'EVENT' | 'HOLIDAY'

/** Entry of «Termine der Schule». Single-day entries have `startDate === endDate`. */
export interface SchoolEvent {
  title: string
  /** ISO date (`YYYY-MM-DD`). */
  startDate: string
  /** ISO date (`YYYY-MM-DD`), inclusive. */
  endDate: string
  kind: EventKind
}

/** Content of the «Prävention» box. */
export interface Prevention {
  title: string
  text: string
  linkText: string
  url: string
}

/** Link of «Meine Links» or «Quick Links». Internal links are app paths, external ones absolute URLs. */
export interface DashboardLink {
  label: string
  url: string
  external: boolean
}

/** Response of `GET /api/dashboard`. */
export interface Dashboard {
  news: NewsItem[]
  events: SchoolEvent[]
  prevention: Prevention
  myLinks: DashboardLink[]
  quickLinks: DashboardLink[]
}
