import { useCallback, useEffect, useState } from 'react'
import {
  DARK_SCHEME_QUERY,
  applyTheme,
  readPreference,
  resolveTheme,
  storePreference,
  systemPrefersDark,
  type ResolvedTheme,
  type ThemePreference,
} from './theme'

export interface ThemeState {
  preference: ThemePreference
  resolved: ResolvedTheme
  setPreference: (preference: ThemePreference) => void
}

/**
 * Current theme preference and the theme that results from it. Applies the theme to the document,
 * persists explicit choices and follows the operating system while the preference is «system».
 */
export function useTheme(): ThemeState {
  const [preference, setPreferenceState] = useState<ThemePreference>(() => readPreference())
  const [systemDark, setSystemDark] = useState<boolean>(() => systemPrefersDark())
  const resolved = resolveTheme(preference, systemDark)

  useEffect(() => {
    applyTheme(resolved)
  }, [resolved])

  useEffect(() => {
    if (preference !== 'system' || typeof window.matchMedia !== 'function') {
      return undefined
    }
    const query = window.matchMedia(DARK_SCHEME_QUERY)
    const onChange = (event: MediaQueryListEvent) => setSystemDark(event.matches)
    query.addEventListener('change', onChange)
    return () => query.removeEventListener('change', onChange)
  }, [preference])

  const setPreference = useCallback((next: ThemePreference) => {
    storePreference(next)
    setPreferenceState(next)
  }, [])

  return { preference, resolved, setPreference }
}
