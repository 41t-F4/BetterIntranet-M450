import { screen } from '@testing-library/react'
import { afterEach, describe, expect, it, vi } from 'vitest'
import { installMatchMedia } from '../../test/matchMedia'
import { currentUserFixture } from '../../test/fixtures/currentUser'
import { renderWithRouter } from '../../test/render'
import { TopBar } from './TopBar'

describe('TopBar', () => {
  it('shows the product name as link to the home page', () => {
    renderWithRouter(<TopBar user={currentUserFixture} />)

    expect(screen.getByRole('link', { name: 'BetterIntranet' })).toHaveAttribute('href', '/')
  })

  it('shows the TBZ wordmark', () => {
    renderWithRouter(<TopBar user={currentUserFixture} />)

    expect(screen.getByText('TBZ')).toBeInTheDocument()
  })

  it('shows the display name and class of the logged-in user', () => {
    renderWithRouter(<TopBar user={currentUserFixture} />)

    expect(screen.getByText('Nicola Wehrli')).toBeInTheDocument()
    expect(screen.getByText('AP 24b')).toBeInTheDocument()
  })

  it('renders without user information while the user is not loaded', () => {
    renderWithRouter(<TopBar user={null} />)

    expect(screen.getByRole('link', { name: 'BetterIntranet' })).toBeInTheDocument()
    expect(screen.queryByText('Nicola Wehrli')).not.toBeInTheDocument()
  })

  it('shows a disabled logout button explained by an SSO tooltip', () => {
    renderWithRouter(<TopBar user={currentUserFixture} />)

    const logout = screen.getByRole('button', { name: 'Abmelden' })
    expect(logout).toBeDisabled()
    expect(logout).toHaveAttribute('title', 'SSO')
  })

  it('uses a banner landmark', () => {
    renderWithRouter(<TopBar user={currentUserFixture} />)

    expect(screen.getByRole('banner')).toBeInTheDocument()
  })

  describe('theme switch', () => {
    afterEach(() => {
      vi.unstubAllGlobals()
      localStorage.clear()
      delete document.documentElement.dataset.theme
    })

    it('offers a theme switch in the header', () => {
      installMatchMedia(false)
      renderWithRouter(<TopBar user={currentUserFixture} />)

      expect(screen.getByRole('button', { name: 'Dunkles Design aktivieren' })).toBeInTheDocument()
    })
  })
})
