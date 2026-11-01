import { useCallback, useEffect, useMemo, useState, type ReactNode } from 'react'
import { apiGet } from '../../shared/api/client'
import type { CurrentUser, ModuleName, ModuleStatus } from '../../shared/api/types'
import { AppDataContext, type AppData } from './useAppData'

type LoadState =
  | { status: 'loading' }
  | { status: 'ready'; user: CurrentUser; modules: ModuleStatus[] }
  | { status: 'error'; error: Error }

const NO_MODULES: readonly ModuleStatus[] = []

/** Loads `/api/me` and `/api/modules` once at startup and provides them to the whole app. */
export function AppDataProvider({ children }: { children: ReactNode }) {
  const [state, setState] = useState<LoadState>({ status: 'loading' })

  useEffect(() => {
    let cancelled = false
    loadStartupData()
      .then((data) => {
        if (!cancelled) setState({ status: 'ready', ...data })
      })
      .catch((reason: unknown) => {
        if (!cancelled) setState({ status: 'error', error: toError(reason) })
      })
    return () => {
      cancelled = true
    }
  }, [])

  const modules = state.status === 'ready' ? state.modules : NO_MODULES

  const isModuleEnabled = useCallback(
    (name: ModuleName) => modules.some((module) => module.name === name && module.enabled),
    [modules],
  )

  const value = useMemo<AppData>(
    () => ({
      status: state.status,
      error: state.status === 'error' ? state.error : null,
      user: state.status === 'ready' ? state.user : null,
      modules,
      isModuleEnabled,
    }),
    [state, modules, isModuleEnabled],
  )

  return <AppDataContext.Provider value={value}>{children}</AppDataContext.Provider>
}

async function loadStartupData(): Promise<{ user: CurrentUser; modules: ModuleStatus[] }> {
  const [user, modules] = await Promise.all([apiGet<CurrentUser>('/api/me'), apiGet<unknown>('/api/modules')])
  if (!Array.isArray(modules)) {
    throw new Error('Ungültige Antwort von /api/modules: Liste erwartet.')
  }
  return { user, modules: modules as ModuleStatus[] }
}

function toError(reason: unknown): Error {
  return reason instanceof Error ? reason : new Error(String(reason))
}
