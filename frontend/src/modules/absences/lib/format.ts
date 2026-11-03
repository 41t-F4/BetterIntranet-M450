/**
 * Pure string formatting of the backend's ISO dates and times. No `Date` objects are involved for the
 * output, so it never depends on the time zone of the browser. Malformed input is a contract violation
 * of the backend and raises a `RangeError` instead of being printed as garbage.
 */

const ISO_DATE = /^(\d{4})-(\d{2})-(\d{2})$/
const ISO_TIME = /^([01]\d|2[0-3]):([0-5]\d)(?::[0-5]\d)?$/

/** Formats an ISO date (`2026-10-12`) as Swiss date (`12.10.2026`). */
export function formatDate(isoDate: string): string {
  const match = ISO_DATE.exec(isoDate)
  if (!match || !isExistingDate(Number(match[1]), Number(match[2]), Number(match[3]))) {
    throw new RangeError(`Ungültiges Datum: "${isoDate}"`)
  }
  const [, year, month, day] = match
  return `${day}.${month}.${year}`
}

/** Formats an ISO date-time (`2026-10-12T10:00:00`) as `12.10.2026 10:00`. */
export function formatDateTime(isoDateTime: string): string {
  const separator = isoDateTime.indexOf('T')
  if (separator < 0) {
    throw new RangeError(`Ungültiger Zeitpunkt: "${isoDateTime}"`)
  }
  return `${formatDate(isoDateTime.slice(0, separator))} ${formatTime(isoDateTime.slice(separator + 1))}`
}

/** Formats a lesson time range (`08:10:00`, `09:45:00`) as `08:10–09:45`. */
export function formatTimeRange(start: string, end: string): string {
  return `${formatTime(start)}–${formatTime(end)}`
}

/** Reduces `HH:mm:ss` (or `HH:mm`) to `HH:mm`. */
function formatTime(time: string): string {
  if (!ISO_TIME.test(time)) {
    throw new RangeError(`Ungültige Uhrzeit: "${time}"`)
  }
  return time.slice(0, 5)
}

/** Rejects calendar dates that do not exist, e.g. `2026-02-30` or month 13. */
function isExistingDate(year: number, month: number, day: number): boolean {
  const date = new Date(Date.UTC(year, month - 1, day))
  return date.getUTCFullYear() === year && date.getUTCMonth() === month - 1 && date.getUTCDate() === day
}
