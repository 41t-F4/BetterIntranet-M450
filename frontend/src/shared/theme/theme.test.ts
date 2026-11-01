import { afterEach, describe, expect, it } from 'vitest'
import { THEME_STORAGE_KEY, applyTheme, readPreference, resolveTheme, storePreference } from './theme'

function memoryStorage(initial: Record<string, string> = {}) {
  const data = new Map(Object.entries(initial))
  return {
    getItem: (key: string) => data.get(key) ?? null,
    setItem: (key: string, value: string) => void data.set(key, value),
    removeItem: (key: string) => void data.delete(key),
    data,
  }
}

const throwingStorage = {
  getItem: () => {
    throw new Error('blocked')
  },
  setItem: () => {
    throw new Error('blocked')
  },
  removeItem: () => {
    throw new Error('blocked')
  },
}

describe('resolveTheme', () => {
  it('uses an explicit preference regardless of the system setting', () => {
    expect(resolveTheme('dark', false)).toBe('dark')
    expect(resolveTheme('light', true)).toBe('light')
  })

  it('follows the system setting for «system»', () => {
    expect(resolveTheme('system', true)).toBe('dark')
    expect(resolveTheme('system', false)).toBe('light')
  })
})

describe('readPreference', () => {
  it('returns the stored preference', () => {
    expect(readPreference(memoryStorage({ [THEME_STORAGE_KEY]: 'dark' }))).toBe('dark')
    expect(readPreference(memoryStorage({ [THEME_STORAGE_KEY]: 'light' }))).toBe('light')
  })

  it('falls back to «system» when nothing or garbage is stored', () => {
    expect(readPreference(memoryStorage())).toBe('system')
    expect(readPreference(memoryStorage({ [THEME_STORAGE_KEY]: 'purple' }))).toBe('system')
  })

  it('falls back to «system» when storage is unavailable or throws', () => {
    expect(readPreference(null)).toBe('system')
    expect(readPreference(throwingStorage)).toBe('system')
  })
})

describe('storePreference', () => {
  it('stores explicit preferences', () => {
    const storage = memoryStorage()
    storePreference('dark', storage)
    expect(storage.data.get(THEME_STORAGE_KEY)).toBe('dark')
  })

  it('removes the entry for «system» so the OS setting applies again', () => {
    const storage = memoryStorage({ [THEME_STORAGE_KEY]: 'dark' })
    storePreference('system', storage)
    expect(storage.data.has(THEME_STORAGE_KEY)).toBe(false)
  })

  it('ignores storage failures', () => {
    expect(() => storePreference('dark', throwingStorage)).not.toThrow()
    expect(() => storePreference('dark', null)).not.toThrow()
  })
})

describe('applyTheme', () => {
  afterEach(() => {
    delete document.documentElement.dataset.theme
    document.documentElement.style.colorScheme = ''
  })

  it('sets the theme attribute and the colour scheme on the root element', () => {
    applyTheme('dark')
    expect(document.documentElement).toHaveAttribute('data-theme', 'dark')
    expect(document.documentElement.style.colorScheme).toBe('dark')

    applyTheme('light')
    expect(document.documentElement).toHaveAttribute('data-theme', 'light')
    expect(document.documentElement.style.colorScheme).toBe('light')
  })
})
