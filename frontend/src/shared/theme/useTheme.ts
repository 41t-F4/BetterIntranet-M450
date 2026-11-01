import type { ResolvedTheme, ThemePreference } from './theme'

export interface ThemeState {
  preference: ThemePreference
  resolved: ResolvedTheme
  setPreference: (preference: ThemePreference) => void
}

export function useTheme(): ThemeState {
  throw new Error('not implemented')
}
