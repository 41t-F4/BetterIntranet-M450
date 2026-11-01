import type { UserRole } from '../../../shared/api/types'

const ROLE_LABELS: Readonly<Record<UserRole, string>> = {
  STUDENT: 'Lernende/r',
  TEACHER: 'Lehrperson',
  ADMIN: 'Administrator/in',
}

/** German display label of a user role (`STUDENT` → `Lernende/r`). */
export function roleLabel(role: UserRole): string {
  return ROLE_LABELS[role]
}

/** Count badge text such as `1 Kurs` or `6 Kurse`. */
export function courseCountLabel(count: number): string {
  return `${count} ${count === 1 ? 'Kurs' : 'Kurse'}`
}
