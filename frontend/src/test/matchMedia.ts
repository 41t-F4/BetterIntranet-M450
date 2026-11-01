import { vi } from 'vitest'

export interface MatchMediaController {
  /** Simulates the operating system switching between light and dark. */
  setPrefersDark: (dark: boolean) => void
}

/**
 * jsdom has no `matchMedia`. Installs a controllable stub for `(prefers-color-scheme: dark)`;
 * restore with `vi.unstubAllGlobals()`.
 */
export function installMatchMedia(prefersDark: boolean): MatchMediaController {
  let dark = prefersDark
  const listeners = new Set<(event: MediaQueryListEvent) => void>()
  const mediaQueryList = (query: string) =>
    ({
      get matches() {
        return query.includes('dark') ? dark : false
      },
      media: query,
      onchange: null,
      addEventListener: (_type: string, listener: (event: MediaQueryListEvent) => void) => listeners.add(listener),
      removeEventListener: (_type: string, listener: (event: MediaQueryListEvent) => void) => listeners.delete(listener),
      addListener: vi.fn(),
      removeListener: vi.fn(),
      dispatchEvent: vi.fn(),
    }) as unknown as MediaQueryList
  vi.stubGlobal('matchMedia', vi.fn(mediaQueryList))
  return {
    setPrefersDark(next: boolean) {
      dark = next
      listeners.forEach((listener) => listener({ matches: next } as MediaQueryListEvent))
    },
  }
}
