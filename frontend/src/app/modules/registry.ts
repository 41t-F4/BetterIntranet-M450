import type { RouteObject } from 'react-router-dom'
import type { ModuleStatus } from '../../shared/api/types'
import { absencesModule } from '../../modules/absences'
import { classesCoursesModule } from '../../modules/classes-courses'
import { dashboardModule } from '../../modules/dashboard'
import { gradesModule } from '../../modules/grades'
import { timetableModule } from '../../modules/timetable'
import type { ModuleDefinition, ModuleRouteHandle, NavItem } from './types'

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

export function createModuleRegistry(definitions: readonly ModuleDefinition[]): ModuleRegistry {
  const isEnabled: ModuleRegistry['isEnabled'] = (definition, modules) => {
    const backendModules = definition.enabledBy ?? [definition.name]
    return backendModules.some((name) => modules.some((status) => status.name === name && status.enabled))
  }

  return {
    getDefinitions: () => definitions,
    isEnabled,
    getNavItems: (modules) =>
      definitions
        .filter((definition) => isEnabled(definition, modules))
        .flatMap((definition) => (definition.navItem ? [definition.navItem] : []))
        .sort((a, b) => a.order - b.order),
    getRoutes: () =>
      definitions.flatMap((definition) => definition.routes.map((route) => annotateRoute(route, definition))),
  }
}

function annotateRoute(route: RouteObject, definition: ModuleDefinition): RouteObject {
  const existing = (route.handle ?? {}) as ModuleRouteHandle
  const handle: ModuleRouteHandle = {
    ...existing,
    module: definition,
    breadcrumb: existing.breadcrumb ?? definition.navItem?.label,
  }
  return { ...route, handle } as RouteObject
}

/** Registry with every feature module of the application. */
export const moduleRegistry: ModuleRegistry = createModuleRegistry([
  dashboardModule,
  classesCoursesModule,
  timetableModule,
  absencesModule,
  gradesModule,
])
