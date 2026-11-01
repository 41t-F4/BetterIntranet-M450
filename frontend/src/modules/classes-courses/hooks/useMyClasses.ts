import { fetchMyClasses } from '../api/classesApi'
import type { SchoolClass } from '../types'
import { useModuleResource, type ModuleResourceState } from './useModuleResource'

/** Classes of the logged-in user; skips the request while the `classes` module is disabled. */
export function useMyClasses(): ModuleResourceState<SchoolClass[]> {
  return useModuleResource('classes', fetchMyClasses)
}
