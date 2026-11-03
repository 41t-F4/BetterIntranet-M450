const MORNING_ENDS_AT_HOUR = 11
const EVENING_STARTS_AT_HOUR = 18

/**
 * Time-of-day greeting for the dashboard, e.g. «Guten Tag, Nicola».
 * «Guten Morgen» before 11:00, «Guten Abend» from 18:00, «Guten Tag» in between (local time).
 * The name is omitted while it is unknown or blank.
 */
export function greeting(date: Date, firstName?: string | null): string {
  const hour = date.getHours()
  const salutation =
    hour < MORNING_ENDS_AT_HOUR ? 'Guten Morgen' : hour < EVENING_STARTS_AT_HOUR ? 'Guten Tag' : 'Guten Abend'
  const name = firstName?.trim()
  return name ? `${salutation}, ${name}` : salutation
}
