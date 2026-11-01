import type { CurrentUser } from '../../shared/api/types'

export interface TopBarProps {
  /** Logged-in user, or `null` while loading / when unavailable. */
  user: CurrentUser | null
}

export function TopBar(_props: TopBarProps) {
  return null
}
