import type { CurrentUser, ModuleName, ModuleStatus } from '../../shared/api/types'

export type AppDataStatus = 'loading' | 'ready' | 'error'

export interface AppData {
  status: AppDataStatus
  /** Set when `status === 'error'`. */
  error: Error | null
  /** Logged-in user, available once `status === 'ready'`. */
  user: CurrentUser | null
  /** Module statuses as reported by `GET /api/modules`. Empty until ready. */
  modules: readonly ModuleStatus[]
  /** Whether the backend reports the given module as enabled. */
  isModuleEnabled(name: ModuleName): boolean
}

export function useAppData(): AppData {
  throw new Error('not implemented')
}

export function useCurrentUser(): CurrentUser | null {
  throw new Error('not implemented')
}

export function useModules(): Pick<AppData, 'modules' | 'isModuleEnabled'> {
  throw new Error('not implemented')
}
