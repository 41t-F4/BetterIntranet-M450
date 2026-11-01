import { render, screen } from '@testing-library/react'
import { describe, expect, it } from 'vitest'
import { dashboardFixture } from '../../../test/fixtures/dashboard'
import { PreventionCard } from './PreventionCard'

describe('PreventionCard', () => {
  it('shows title and text', () => {
    render(<PreventionCard prevention={dashboardFixture.prevention} />)

    expect(screen.getByRole('heading', { name: 'Prävention' })).toBeInTheDocument()
    expect(screen.getByText('147.ch unterstützt junge Menschen bei Sorgen & Problemen.')).toBeInTheDocument()
  })

  it('opens the external link safely in a new tab', () => {
    render(<PreventionCard prevention={dashboardFixture.prevention} />)

    const link = screen.getByRole('link', { name: 'www.147.ch (öffnet in neuem Tab)' })
    expect(link).toHaveAttribute('href', 'https://www.147.ch')
    expect(link).toHaveAttribute('target', '_blank')
    expect(link).toHaveAttribute('rel', 'noopener noreferrer')
  })
})
