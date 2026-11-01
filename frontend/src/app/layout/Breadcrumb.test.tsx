import { render, screen, within } from '@testing-library/react'
import { RouterProvider, createMemoryRouter, type RouteObject } from 'react-router-dom'
import { describe, expect, it } from 'vitest'
import type { ModuleRouteHandle } from '../modules/types'
import { Breadcrumb } from './Breadcrumb'

const handle = (breadcrumb: string): ModuleRouteHandle => ({ breadcrumb })

const routes: RouteObject[] = [
  {
    path: '/',
    element: <Breadcrumb />,
    children: [
      { path: '/', element: null, handle: handle('Home') },
      {
        path: '/noten',
        element: null,
        handle: handle('Noten'),
        children: [{ path: 'bms', element: null, handle: handle('BMS') }],
      },
      { path: '/stundenplan', element: null },
    ],
  },
]

function renderAt(path: string) {
  return render(<RouterProvider router={createMemoryRouter(routes, { initialEntries: [path] })} />)
}

describe('Breadcrumb', () => {
  it('renders a navigation landmark labelled Breadcrumb', () => {
    renderAt('/noten')

    expect(screen.getByRole('navigation', { name: 'Breadcrumb' })).toBeInTheDocument()
  })

  it('shows Home followed by the label of the current route', () => {
    renderAt('/noten')

    const items = within(screen.getByRole('navigation', { name: 'Breadcrumb' })).getAllByRole('listitem')

    expect(items.map((item) => item.textContent)).toEqual(['Home', 'Noten'])
  })

  it('links Home to the start page and marks the last crumb as current', () => {
    renderAt('/noten')

    expect(screen.getByRole('link', { name: 'Home' })).toHaveAttribute('href', '/')
    expect(screen.getByText('Noten')).toHaveAttribute('aria-current', 'page')
  })

  it('shows only Home on the start page without duplicating it', () => {
    renderAt('/')

    const items = within(screen.getByRole('navigation', { name: 'Breadcrumb' })).getAllByRole('listitem')

    expect(items.map((item) => item.textContent)).toEqual(['Home'])
  })

  it('shows nested crumbs for nested routes and links the intermediate ones', () => {
    renderAt('/noten/bms')

    const items = within(screen.getByRole('navigation', { name: 'Breadcrumb' })).getAllByRole('listitem')

    expect(items.map((item) => item.textContent)).toEqual(['Home', 'Noten', 'BMS'])
    expect(screen.getByRole('link', { name: 'Noten' })).toHaveAttribute('href', '/noten')
  })

  it('shows only Home for routes without a breadcrumb label', () => {
    renderAt('/stundenplan')

    const items = within(screen.getByRole('navigation', { name: 'Breadcrumb' })).getAllByRole('listitem')

    expect(items.map((item) => item.textContent)).toEqual(['Home'])
  })
})
