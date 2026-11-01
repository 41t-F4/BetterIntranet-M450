import { screen } from '@testing-library/react'
import { describe, expect, it } from 'vitest'
import { renderWithRouter } from '../../../test/render'
import type { DashboardLink } from '../types'
import { LinksCard } from './LinksCard'

const links: DashboardLink[] = [
  { label: 'Noten', url: '/noten', external: false },
  { label: 'Service Desk Pro', url: 'https://servicedesk.tbz.ch', external: true },
]

describe('LinksCard', () => {
  it('uses the given title as heading', () => {
    renderWithRouter(<LinksCard title="Meine Links" links={links} />)

    expect(screen.getByRole('heading', { name: 'Meine Links' })).toBeInTheDocument()
  })

  it('renders internal links as in-app links without a new tab', () => {
    renderWithRouter(<LinksCard title="Meine Links" links={links} />)

    const link = screen.getByRole('link', { name: 'Noten' })
    expect(link).toHaveAttribute('href', '/noten')
    expect(link).not.toHaveAttribute('target')
    expect(link).not.toHaveAttribute('rel')
  })

  it('opens external links safely in a new tab and announces it to screen readers', () => {
    renderWithRouter(<LinksCard title="Quick Links" links={links} />)

    const link = screen.getByRole('link', { name: 'Service Desk Pro (öffnet in neuem Tab)' })
    expect(link).toHaveAttribute('href', 'https://servicedesk.tbz.ch')
    expect(link).toHaveAttribute('target', '_blank')
    expect(link).toHaveAttribute('rel', 'noopener noreferrer')
  })

  it('renders the links as a list in the given order', () => {
    renderWithRouter(<LinksCard title="Quick Links" links={links} />)

    expect(screen.getAllByRole('listitem').map((item) => item.textContent)).toEqual([
      'Noten',
      'Service Desk Pro (öffnet in neuem Tab)',
    ])
  })

  it('shows a message when there are no links', () => {
    renderWithRouter(<LinksCard title="Quick Links" links={[]} />)

    expect(screen.getByText('Keine Links vorhanden.')).toBeInTheDocument()
    expect(screen.queryByRole('list')).not.toBeInTheDocument()
  })
})
