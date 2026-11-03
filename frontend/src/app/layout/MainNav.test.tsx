import { screen, within } from '@testing-library/react'
import { describe, expect, it } from 'vitest'
import { renderWithRouter } from '../../test/render'
import type { NavItem } from '../modules/types'
import { MainNav } from './MainNav'

const items: NavItem[] = [
  { label: 'Home', path: '/', order: 10 },
  { label: 'Stundenplan', path: '/stundenplan', order: 30 },
  { label: 'Noten', path: '/noten', order: 50 },
]

describe('MainNav', () => {
  it('renders a navigation landmark labelled Hauptnavigation', () => {
    renderWithRouter(<MainNav items={items} />)

    expect(screen.getByRole('navigation', { name: 'Hauptnavigation' })).toBeInTheDocument()
  })

  it('renders one link per item in the given order', () => {
    renderWithRouter(<MainNav items={items} />)

    const links = within(screen.getByRole('navigation')).getAllByRole('link')

    expect(links.map((link) => link.textContent)).toEqual(['Home', 'Stundenplan', 'Noten'])
    expect(links.map((link) => link.getAttribute('href'))).toEqual(['/', '/stundenplan', '/noten'])
  })

  it('marks the link of the current route as active', () => {
    renderWithRouter(<MainNav items={items} />, { path: '/noten' })

    expect(screen.getByRole('link', { name: 'Noten' })).toHaveAttribute('aria-current', 'page')
    expect(screen.getByRole('link', { name: 'Home' })).not.toHaveAttribute('aria-current')
  })

  it('marks Home as active only on the exact home path', () => {
    renderWithRouter(<MainNav items={items} />, { path: '/stundenplan' })

    expect(screen.getByRole('link', { name: 'Home' })).not.toHaveAttribute('aria-current')
    expect(screen.getByRole('link', { name: 'Stundenplan' })).toHaveAttribute('aria-current', 'page')
  })

  it('renders an empty navigation when no module is enabled', () => {
    renderWithRouter(<MainNav items={[]} />)

    expect(within(screen.getByRole('navigation')).queryAllByRole('link')).toHaveLength(0)
  })
})
