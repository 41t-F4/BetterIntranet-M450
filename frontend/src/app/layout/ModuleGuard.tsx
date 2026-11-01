import { Outlet, useMatches } from 'react-router-dom'
import type { ModuleRegistry } from '../modules/registry'
import type { ModuleRouteHandle } from '../modules/types'
import { useModules } from '../providers/useAppData'
import { DisabledModulePage } from './DisabledModulePage'

export interface ModuleGuardProps {
  registry: ModuleRegistry
}

/**
 * Layout route around all module routes: renders the matched module page, or the disabled-module
 * card when the backend reports the module of the deepest matched route as disabled.
 */
export function ModuleGuard({ registry }: ModuleGuardProps) {
  const { modules } = useModules()
  const matches = useMatches()
  const definition = matches
    .map((match) => (match.handle as ModuleRouteHandle | undefined)?.module)
    .filter((module) => module !== undefined)
    .at(-1)

  if (definition && !registry.isEnabled(definition, modules)) {
    return <DisabledModulePage title={definition.navItem?.label ?? definition.name} />
  }
  return <Outlet />
}
