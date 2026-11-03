import type { ModuleName, ModuleStatus } from '../../shared/api/types'

const ALL_MODULES: readonly ModuleName[] = [
  'dashboard',
  'timetable',
  'classes',
  'courses',
  'absences',
  'grades',
  'user',
]

/** `GET /api/modules` response with every module enabled. */
export const allModulesEnabled: ModuleStatus[] = ALL_MODULES.map((name) => ({ name, enabled: true }))

/** `GET /api/modules` response with the given modules disabled and all others enabled. */
export function modulesWithDisabled(...disabled: ModuleName[]): ModuleStatus[] {
  return ALL_MODULES.map((name) => ({ name, enabled: !disabled.includes(name) }))
}
