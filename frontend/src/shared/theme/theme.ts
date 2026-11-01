/** What the user chose: an explicit theme or «follow the operating system». */
export type ThemePreference = 'light' | 'dark' | 'system'

/** The theme that is actually applied. */
export type ResolvedTheme = 'light' | 'dark'

export const THEME_STORAGE_KEY = 'betterintranet.theme'
export const DARK_SCHEME_QUERY = '(prefers-color-scheme: dark)'

export function resolveTheme(_preference: ThemePreference, _systemPrefersDark: boolean): ResolvedTheme {
  throw new Error('not implemented')
}

export function readPreference(_storage?: Pick<Storage, 'getItem'> | null): ThemePreference {
  throw new Error('not implemented')
}

export function storePreference(_preference: ThemePreference, _storage?: Pick<Storage, 'setItem' | 'removeItem'> | null): void {
  throw new Error('not implemented')
}

export function applyTheme(_theme: ResolvedTheme, _root?: HTMLElement): void {
  throw new Error('not implemented')
}
