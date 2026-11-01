import type { UserRole } from '../../../shared/api/types'
import type { SchoolPart } from '../types'

/** German display label of a school part (`UEK` → `ÜK`). */
export function schoolPartLabel(_schoolPart: SchoolPart): string {
  throw new Error('Not implemented')
}

/** German display label of a user role (`STUDENT` → `Lernende/r`). */
export function roleLabel(_role: UserRole): string {
  throw new Error('Not implemented')
}

/** Count badge text such as `1 Kurs` or `6 Kurse`. */
export function courseCountLabel(_count: number): string {
  throw new Error('Not implemented')
}
