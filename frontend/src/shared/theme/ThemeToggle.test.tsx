import { render, screen } from '@testing-library/react'
import userEvent from '@testing-library/user-event'
import { afterEach, beforeEach, describe, expect, it, vi } from 'vitest'
import { installMatchMedia } from '../../test/matchMedia'
import { ThemeToggle } from './ThemeToggle'

describe('ThemeToggle', () => {
  beforeEach(() => localStorage.clear())
  afterEach(() => {
    vi.unstubAllGlobals()
    localStorage.clear()
    delete document.documentElement.dataset.theme
  })

  it('offers the dark theme while the light theme is active', () => {
    installMatchMedia(false)
    render(<ThemeToggle />)

    expect(screen.getByRole('button', { name: 'Dunkles Design aktivieren' })).toBeInTheDocument()
  })

  it('offers the light theme while the dark theme is active', () => {
    installMatchMedia(true)
    render(<ThemeToggle />)

    expect(screen.getByRole('button', { name: 'Helles Design aktivieren' })).toBeInTheDocument()
  })

  it('switches the theme on click and remembers it', async () => {
    installMatchMedia(false)
    render(<ThemeToggle />)

    await userEvent.click(screen.getByRole('button', { name: 'Dunkles Design aktivieren' }))

    expect(document.documentElement).toHaveAttribute('data-theme', 'dark')
    expect(screen.getByRole('button', { name: 'Helles Design aktivieren' })).toBeInTheDocument()
    expect(localStorage.getItem('betterintranet.theme')).toBe('dark')
  })
})
