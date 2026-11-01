import type { RouteObject } from 'react-router-dom'
import type { ModuleName } from '../../shared/api/types'

/** Entry in the main navigation. Items are sorted by `order`, lowest first. */
export interface NavItem {
  label: string
  /** Absolute path, e.g. `/noten`. Also used as breadcrumb target. */
  path: string
  order: number
}

/**
 * Contract every feature module (`src/modules/<name>/index.ts`) exports and registers in the
 * module registry. The shell derives navigation, routes, breadcrumbs and module enable/disable
 * handling from it.
 */
export interface ModuleDefinition {
  /** Backend module name; must match `GET /api/modules`. */
  name: ModuleName
  /**
   * Backend modules that control visibility. The module counts as enabled as soon as one of them
   * is enabled. Defaults to `[name]`. Used by combined modules such as «Klassen & Kurse».
   */
  enabledBy?: readonly ModuleName[]
  /** Optional main navigation entry. Modules without one are reachable by URL only. */
  navItem?: NavItem
  /**
   * Routes of the module (absolute paths). Each route may set `handle.breadcrumb` to override the
   * breadcrumb label, which defaults to the nav item label.
   */
  routes: RouteObject[]
}

/** `handle` attached to every module route by the registry; read via `useMatches()`. */
export interface ModuleRouteHandle {
  /** Module the route belongs to. Set by the registry. */
  module?: ModuleDefinition
  /** Breadcrumb label of the route. */
  breadcrumb?: string
}
