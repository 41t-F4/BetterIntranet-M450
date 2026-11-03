import { act, renderHook } from '@testing-library/react'
import { afterEach, beforeEach, describe, expect, it, vi } from 'vitest'
import { installMatchMedia } from '../../test/matchMedia'
import { THEME_STORAGE_KEY } from './theme'
import { useTheme } from './useTheme'

describe('useTheme', () => {
  beforeEach(() => localStorage.clear())
  afterEach(() => {
    vi.unstubAllGlobals()
    localStorage.clear()
    delete document.documentElement.dataset.theme
  })

  it('follows the system setting when nothing is stored', () => {
    installMatchMedia(true)

    const { result } = renderHook(() => useTheme())

    expect(result.current.preference).toBe('system')
    expect(result.current.resolved).toBe('dark')
    expect(document.documentElement).toHaveAttribute('data-theme', 'dark')
  })

  it('uses a stored preference over the system setting', () => {
    installMatchMedia(true)
    localStorage.setItem(THEME_STORAGE_KEY, 'light')

    const { result } = renderHook(() => useTheme())

    expect(result.current.resolved).toBe('light')
    expect(document.documentElement).toHaveAttribute('data-theme', 'light')
  })

  it('applies and stores a new preference', () => {
    installMatchMedia(false)
    const { result } = renderHook(() => useTheme())

    act(() => result.current.setPreference('dark'))

    expect(result.current.resolved).toBe('dark')
    expect(localStorage.getItem(THEME_STORAGE_KEY)).toBe('dark')
    expect(document.documentElement).toHaveAttribute('data-theme', 'dark')
  })

  it('reacts to the operating system switching while following the system', () => {
    const system = installMatchMedia(false)
    const { result } = renderHook(() => useTheme())

    act(() => system.setPrefersDark(true))

    expect(result.current.resolved).toBe('dark')
    expect(document.documentElement).toHaveAttribute('data-theme', 'dark')
  })

  it('ignores operating system changes once the user picked a theme', () => {
    const system = installMatchMedia(false)
    const { result } = renderHook(() => useTheme())
    act(() => result.current.setPreference('light'))

    act(() => system.setPrefersDark(true))

    expect(result.current.resolved).toBe('light')
  })
})
