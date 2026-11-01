import { createContext, useContext } from 'react'
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

export const AppDataContext = createContext<AppData | null>(null)

/** Startup data (user, module statuses) and its loading state. Must be used below `AppDataProvider`. */
export function useAppData(): AppData {
  const data = useContext(AppDataContext)
  if (data === null) {
    throw new Error('useAppData must be used within an AppDataProvider')
  }
  return data
}

/** The logged-in user, or `null` until loaded. */
export function useCurrentUser(): CurrentUser | null {
  return useAppData().user
}

/** Module statuses from `GET /api/modules` and a helper to check a single module. */
export function useModules(): Pick<AppData, 'modules' | 'isModuleEnabled'> {
  const { modules, isModuleEnabled } = useAppData()
  return { modules, isModuleEnabled }
}
