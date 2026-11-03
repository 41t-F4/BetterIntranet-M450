import type { CurrentUser } from '../../shared/api/types'

/** Default logged-in user, matches the backend default (`X-User: nicola.wehrli`). */
export const currentUserFixture: CurrentUser = {
  username: 'nicola.wehrli',
  displayName: 'Nicola Wehrli',
  firstName: 'Nicola',
  lastName: 'Wehrli',
  email: 'nicola.wehrli@tbz.ch',
  className: 'AP 24b',
  classId: 7471,
  role: 'STUDENT',
}
