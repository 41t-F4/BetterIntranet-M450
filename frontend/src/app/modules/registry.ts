import type { RouteObject } from 'react-router-dom'
import type { ModuleStatus } from '../../shared/api/types'
import type { ModuleDefinition, NavItem } from './types'

export interface ModuleRegistry {
  /** All registered module definitions in registration order. */
  getDefinitions(): readonly ModuleDefinition[]
  /** Whether a module counts as enabled according to the given `GET /api/modules` response. */
  isEnabled(definition: ModuleDefinition, modules: readonly ModuleStatus[]): boolean
  /** Nav items of enabled modules, sorted by `order`. */
  getNavItems(modules: readonly ModuleStatus[]): NavItem[]
  /** All module routes, each annotated with a `ModuleRouteHandle`. */
  getRoutes(): RouteObject[]
}

export function createModuleRegistry(_definitions: readonly ModuleDefinition[]): ModuleRegistry {
  throw new Error('not implemented')
}

/** Registry with every feature module of the application. */
export const moduleRegistry: ModuleRegistry = {
  getDefinitions: () => {
    throw new Error('not implemented')
  },
  isEnabled: () => {
    throw new Error('not implemented')
  },
  getNavItems: () => {
    throw new Error('not implemented')
  },
  getRoutes: () => {
    throw new Error('not implemented')
  },
}
