import type { EventKind } from '../types'

const LABELS: Record<EventKind, string> = {
  HOLIDAY: 'Ferien',
  EVENT: 'Anlass',
}

/** German badge label of a school calendar entry kind. */
export function eventKindLabel(kind: EventKind): string {
  return LABELS[kind]
}
