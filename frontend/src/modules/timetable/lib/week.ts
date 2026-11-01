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

/** Formats a local date as `YYYY-MM-DD`. */
export function toIsoDate(_date: Date): IsoDate {
  throw new Error('not implemented')
}

/** Parses `YYYY-MM-DD` as local midnight. */
export function parseIsoDate(_iso: IsoDate): Date {
  throw new Error('not implemented')
}

/** Adds (or subtracts) whole days. */
export function addDays(_iso: IsoDate, _days: number): IsoDate {
  throw new Error('not implemented')
}

/** Monday..Friday of the ISO week containing the given date (Saturday and Sunday belong to the preceding Monday). */
export function weekRange(_date: Date | IsoDate): WeekRange {
  throw new Error('not implemented')
}

/** ISO 8601 week number and week-based year of the given date. */
export function isoWeek(_date: Date | IsoDate): IsoWeek {
  throw new Error('not implemented')
}

/** German week label, e.g. `KW 45 · 2. Nov. – 6. Nov. 2026`. */
export function formatWeekLabel(_weekStart: IsoDate): string {
  throw new Error('not implemented')
}

/** German day label, e.g. `Montag, 2. Nov.`. */
export function formatDayLabel(_iso: IsoDate): string {
  throw new Error('not implemented')
}
