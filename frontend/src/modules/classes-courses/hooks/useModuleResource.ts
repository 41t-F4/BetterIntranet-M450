import { useEffect, useState } from 'react'
import { useAppData } from '../../../app/providers/useAppData'
import { ApiError } from '../../../shared/api/client'
import type { ModuleName } from '../../../shared/api/types'

/** Loading state of data that belongs to one backend module. */
export type ModuleResourceState<T> =
  | { status: 'loading' }
  | { status: 'disabled' }
  | { status: 'ready'; data: T }
  | { status: 'error'; error: Error }

/**
 * Loads data of a backend module once the startup data is available. When `GET /api/modules`
 * reports the module as disabled, no request is made and the state is `disabled`. A 503 for the
 * module (disabled meanwhile) is mapped to `disabled` as well; every other failure is an `error`.
 * `load` must be referentially stable (a module-level function).
 */
export function useModuleResource<T>(module: ModuleName, load: () => Promise<T>): ModuleResourceState<T> {
  const { status: appStatus, isModuleEnabled } = useAppData()
  const enabled = isModuleEnabled(module)
  const [state, setState] = useState<ModuleResourceState<T>>({ status: 'loading' })

  useEffect(() => {
    if (appStatus !== 'ready') {
      return
    }
    if (!enabled) {
      setState({ status: 'disabled' })
      return
    }

    let cancelled = false
    setState({ status: 'loading' })
    load()
      .then((data) => {
        if (!cancelled) setState({ status: 'ready', data })
      })
      .catch((reason: unknown) => {
        if (!cancelled) setState(toFailureState(reason, module))
      })
    return () => {
      cancelled = true
    }
  }, [appStatus, enabled, load, module])

  return state
}

function toFailureState<T>(reason: unknown, module: ModuleName): ModuleResourceState<T> {
  if (reason instanceof ApiError && reason.status === 503 && reason.module === module) {
    return { status: 'disabled' }
  }
  return { status: 'error', error: reason instanceof Error ? reason : new Error(String(reason)) }
}
