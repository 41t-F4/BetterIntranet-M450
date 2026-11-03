/** What the user chose: an explicit theme or «follow the operating system». */
export type ThemePreference = 'light' | 'dark' | 'system'

/** The theme that is actually applied. */
export type ResolvedTheme = 'light' | 'dark'

export const THEME_STORAGE_KEY = 'betterintranet.theme'
export const DARK_SCHEME_QUERY = '(prefers-color-scheme: dark)'

type ReadableStorage = Pick<Storage, 'getItem'>
type WritableStorage = Pick<Storage, 'setItem' | 'removeItem'>

/** `localStorage` if the browser allows access to it (private modes may throw), otherwise `null`. */
function defaultStorage(): Storage | null {
  try {
    return window.localStorage
  } catch {
    return null
  }
}

export function resolveTheme(preference: ThemePreference, systemPrefersDark: boolean): ResolvedTheme {
  if (preference === 'system') {
    return systemPrefersDark ? 'dark' : 'light'
  }
  return preference
}

/** Stored preference, or «system» when nothing valid is stored or storage is unavailable. */
export function readPreference(storage: ReadableStorage | null = defaultStorage()): ThemePreference {
  try {
    const stored = storage?.getItem(THEME_STORAGE_KEY)
    return stored === 'light' || stored === 'dark' ? stored : 'system'
  } catch {
    return 'system'
  }
}

/** Remembers an explicit choice; «system» clears it. Storage failures are ignored on purpose. */
export function storePreference(preference: ThemePreference, storage: WritableStorage | null = defaultStorage()): void {
  try {
    if (preference === 'system') {
      storage?.removeItem(THEME_STORAGE_KEY)
    } else {
      storage?.setItem(THEME_STORAGE_KEY, preference)
    }
  } catch {
    // Without storage the choice simply lasts for the current page view.
  }
}

/**
 * Applies the theme to the root element. CSS tokens switch on `data-theme`; `color-scheme` makes
 * native controls and scrollbars match. The inline script in `index.html` does the same before the
 * first paint to avoid a flash of the wrong theme.
 */
export function applyTheme(theme: ResolvedTheme, root: HTMLElement = document.documentElement): void {
  root.dataset.theme = theme
  root.style.colorScheme = theme
}

/** Whether the operating system currently asks for a dark scheme; `false` where `matchMedia` is missing. */
export function systemPrefersDark(): boolean {
  return typeof window.matchMedia === 'function' && window.matchMedia(DARK_SCHEME_QUERY).matches
}
