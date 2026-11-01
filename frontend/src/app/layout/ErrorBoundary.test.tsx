import { render, screen } from '@testing-library/react'
import { RouterProvider, createMemoryRouter } from 'react-router-dom'
import { afterEach, beforeEach, describe, expect, it, vi } from 'vitest'
import { ErrorBoundary } from './ErrorBoundary'

function Broken(): never {
  throw new Error('Kaputt')
}

describe('ErrorBoundary', () => {
  beforeEach(() => {
    vi.spyOn(console, 'error').mockImplementation(() => {})
  })

  afterEach(() => {
    vi.restoreAllMocks()
  })

  it('shows an error card with the error message instead of crashing', () => {
    const router = createMemoryRouter([{ path: '/', element: <Broken />, errorElement: <ErrorBoundary /> }])

    render(<RouterProvider router={router} />)

    expect(screen.getByRole('alert')).toBeInTheDocument()
    expect(screen.getByRole('heading', { name: 'Es ist ein Fehler aufgetreten.' })).toBeInTheDocument()
    expect(screen.getByText('Kaputt')).toBeInTheDocument()
  })

  it('offers a link back to the start page', () => {
    const router = createMemoryRouter([{ path: '/', element: <Broken />, errorElement: <ErrorBoundary /> }])

    render(<RouterProvider router={router} />)

    expect(screen.getByRole('link', { name: 'Zur Startseite' })).toHaveAttribute('href', '/')
  })
})
