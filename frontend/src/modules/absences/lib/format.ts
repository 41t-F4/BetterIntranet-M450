/**
 * Pure string formatting of the backend's ISO dates and times. No `Date` objects are involved so
 * the output never depends on the time zone of the browser.
 */

/** Formats an ISO date (`2026-10-12`) as Swiss date (`12.10.2026`). */
export function formatDate(isoDate: string): string {
  const [year = '', month = '', day = ''] = isoDate.split('-')
  return `${day}.${month}.${year}`
}

/** Formats an ISO date-time (`2026-10-12T10:00:00`) as `12.10.2026 10:00`. */
export function formatDateTime(isoDateTime: string): string {
  const [date = '', time = ''] = isoDateTime.split('T')
  return `${formatDate(date)} ${formatTime(time)}`
}

/** Formats a lesson time range (`08:10:00`, `09:45:00`) as `08:10–09:45`. */
export function formatTimeRange(start: string, end: string): string {
  return `${formatTime(start)}–${formatTime(end)}`
}

/** Reduces `HH:mm:ss` (or `HH:mm`) to `HH:mm`. */
function formatTime(time: string): string {
  return time.slice(0, 5)
}
