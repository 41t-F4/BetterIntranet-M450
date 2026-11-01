import type { NavItem } from '../modules/types'

export interface MainNavProps {
  /** Items of enabled modules, already sorted (see `ModuleRegistry.getNavItems`). */
  items: readonly NavItem[]
}

export function MainNav(_props: MainNavProps) {
  return null
}
