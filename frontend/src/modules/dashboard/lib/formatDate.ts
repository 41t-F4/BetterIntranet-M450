const ISO_DATE = /^(\d{4})-(\d{2})-(\d{2})$/

/**
 * Formats an ISO date (`YYYY-MM-DD`) as Swiss date `DD.MM.YYYY`.
 * Works on the string directly, so the result never depends on the browser time zone.
 */
export function formatDate(isoDate: string): string {
  const match = ISO_DATE.exec(isoDate)
  if (!match) {
    throw new Error(`Ungültiges Datum: ${isoDate}`)
  }
  const [, year, month, day] = match
  return `${day}.${month}.${year}`
}

/** Formats an inclusive date range («21.12.2026 – 02.01.2027»); a single day is shown as one date. */
export function formatDateRange(startDate: string, endDate: string): string {
  const start = formatDate(startDate)
  return startDate === endDate ? start : `${start} – ${formatDate(endDate)}`
}
