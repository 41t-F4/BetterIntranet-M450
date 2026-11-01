/**
 * Types of the backend contracts consumed by the shell (`/api/me`, `/api/modules`, errors).
 * Feature modules add their own types next to their hooks.
 */

/** Names of the backend modules as reported by `GET /api/modules`. */
export type ModuleName =
  | 'dashboard'
  | 'timetable'
  | 'classes'
  | 'courses'
  | 'absences'
  | 'grades'
  | 'user'

export type UserRole = 'STUDENT' | 'TEACHER' | 'ADMIN'

/** Response of `GET /api/me`. */
export interface CurrentUser {
  username: string
  displayName: string
  firstName: string
  lastName: string
  email: string
  className: string
  classId: number
  role: UserRole
}

/** One entry of `GET /api/modules`. */
export interface ModuleStatus {
  name: ModuleName
  enabled: boolean
}

/** RFC 9457 problem details as returned by the backend for every non-2xx response. */
export interface ProblemDetail {
  type?: string
  title?: string
  status?: number
  detail?: string
  instance?: string
  /** Set by the backend when the error concerns a specific module (e.g. disabled module). */
  module?: ModuleName
}
