/** Formats an ISO date (`2026-10-12`) as Swiss date (`12.10.2026`). */
export function formatDate(isoDate: string): string {
  return isoDate
}

/** Formats an ISO date-time (`2026-10-12T10:00:00`) as `12.10.2026 10:00`. */
export function formatDateTime(isoDateTime: string): string {
  return isoDateTime
}

/** Formats a lesson time range (`08:10:00`, `09:45:00`) as `08:10–09:45`. */
export function formatTimeRange(start: string, end: string): string {
  return `${start}${end}`
}
