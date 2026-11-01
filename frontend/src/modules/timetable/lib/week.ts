import type { IsoDate } from '../types'

/** Monday to Friday of one ISO week. */
export interface WeekRange {
  /** Monday. */
  from: IsoDate
  /** Friday. */
  to: IsoDate
  /** Monday, Tuesday, Wednesday, Thursday, Friday. */
  days: IsoDate[]
}

/** ISO 8601 week number together with its week-based year (which may differ from the calendar year). */
export interface IsoWeek {
  week: number
  year: number
}

const ISO_DATE_PATTERN = /^(\d{4})-(\d{2})-(\d{2})$/
const MS_PER_DAY = 24 * 60 * 60 * 1000
const SCHOOL_DAYS_PER_WEEK = 5

const MONTH_NAMES = ['Jan.', 'Feb.', 'März', 'Apr.', 'Mai', 'Juni', 'Juli', 'Aug.', 'Sept.', 'Okt.', 'Nov.', 'Dez.']
const WEEKDAY_NAMES = ['Sonntag', 'Montag', 'Dienstag', 'Mittwoch', 'Donnerstag', 'Freitag', 'Samstag']

/** Formats a local date as `YYYY-MM-DD`. */
export function toIsoDate(date: Date): IsoDate {
  return `${date.getFullYear()}-${pad(date.getMonth() + 1)}-${pad(date.getDate())}`
}

/** Parses `YYYY-MM-DD` as local midnight. */
export function parseIsoDate(iso: IsoDate): Date {
  const match = ISO_DATE_PATTERN.exec(iso)
  if (!match) {
    throw new Error(`Ungültiges Datum: ${iso}`)
  }
  return new Date(Number(match[1]), Number(match[2]) - 1, Number(match[3]))
}

/** Adds (or subtracts) whole days. */
export function addDays(iso: IsoDate, days: number): IsoDate {
  const date = parseIsoDate(iso)
  date.setDate(date.getDate() + days)
  return toIsoDate(date)
}

/** Monday..Friday of the ISO week containing the given date (Saturday and Sunday belong to the preceding Monday). */
export function weekRange(date: Date | IsoDate): WeekRange {
  const monday = startOfIsoWeek(toDate(date))
  const from = toIsoDate(monday)
  const days = Array.from({ length: SCHOOL_DAYS_PER_WEEK }, (_, index) => addDays(from, index))
  return { from, to: days[SCHOOL_DAYS_PER_WEEK - 1] as IsoDate, days }
}

/** ISO 8601 week number and week-based year of the given date. */
export function isoWeek(date: Date | IsoDate): IsoWeek {
  // The Thursday of a week decides which year the ISO week belongs to.
  const thursday = startOfIsoWeek(toDate(date))
  thursday.setDate(thursday.getDate() + 3)
  const year = thursday.getFullYear()
  const dayOfYear = Math.round((thursday.getTime() - new Date(year, 0, 1).getTime()) / MS_PER_DAY) + 1
  return { week: Math.floor((dayOfYear - 1) / 7) + 1, year }
}

/** German week label, e.g. `KW 45 · 2. Nov. – 6. Nov. 2026`. */
export function formatWeekLabel(weekStart: IsoDate): string {
  const { from, to } = weekRange(weekStart)
  const start = parseIsoDate(from)
  const end = parseIsoDate(to)
  const startYear = start.getFullYear() === end.getFullYear() ? '' : ` ${start.getFullYear()}`
  return `KW ${isoWeek(from).week} · ${formatDayMonth(start)}${startYear} – ${formatDayMonth(end)} ${end.getFullYear()}`
}

/** German day label, e.g. `Montag, 2. Nov.`. */
export function formatDayLabel(iso: IsoDate): string {
  const date = parseIsoDate(iso)
  return `${WEEKDAY_NAMES[date.getDay()]}, ${formatDayMonth(date)}`
}

function toDate(date: Date | IsoDate): Date {
  return typeof date === 'string' ? parseIsoDate(date) : date
}

/** Local midnight of the Monday of the ISO week containing `date`. */
function startOfIsoWeek(date: Date): Date {
  const daysSinceMonday = (date.getDay() + 6) % 7
  return new Date(date.getFullYear(), date.getMonth(), date.getDate() - daysSinceMonday)
}

function formatDayMonth(date: Date): string {
  return `${date.getDate()}. ${MONTH_NAMES[date.getMonth()]}`
}

function pad(value: number): string {
  return String(value).padStart(2, '0')
}
