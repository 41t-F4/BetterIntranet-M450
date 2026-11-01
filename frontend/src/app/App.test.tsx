import { render, screen, within } from '@testing-library/react'
import userEvent from '@testing-library/user-event'
import { HttpResponse, http } from 'msw'
import { afterEach, beforeEach, describe, expect, it, vi } from 'vitest'
import { modulesWithDisabled } from '../test/fixtures/modules'
import { server } from '../test/msw/server'
import { App } from './App'

/** Renders the app with the browser history positioned at the given path. */
function renderApp(path = '/') {
  window.history.pushState({}, '', path)
  return render(<App />)
}

async function navLinks() {
  const nav = await screen.findByRole('navigation', { name: 'Hauptnavigation' })
  return within(nav).getAllByRole('link').map((link) => link.textContent)
}

describe('App', () => {
  afterEach(() => {
    window.history.pushState({}, '', '/')
  })

  it('shows a loading indicator until the startup requests complete', () => {
    renderApp()

    expect(screen.getByRole('status')).toHaveTextContent('Laden')
  })

  it('renders the top bar with the name of the logged-in user from /api/me', async () => {
    renderApp()

    expect(await screen.findByText('Nicola Wehrli')).toBeInTheDocument()
    expect(within(screen.getByRole('banner')).getByText('AP 24b')).toBeInTheDocument()
  })

  it('shows navigation entries for all enabled modules', async () => {
    renderApp()

    expect(await navLinks()).toEqual(['Home', 'Klassen & Kurse', 'Stundenplan', 'Absenzen', 'Noten'])
  })

  it('hides navigation entries of disabled modules', async () => {
    server.use(http.get('/api/modules', () => HttpResponse.json(modulesWithDisabled('grades', 'absences'))))

    renderApp()

    expect(await navLinks()).toEqual(['Home', 'Klassen & Kurse', 'Stundenplan'])
  })

  it('keeps «Klassen & Kurse» when only one of classes and courses is disabled', async () => {
    server.use(http.get('/api/modules', () => HttpResponse.json(modulesWithDisabled('classes'))))

    renderApp()

    expect(await navLinks()).toContain('Klassen & Kurse')
  })

  it('renders the dashboard placeholder on the start page', async () => {
    renderApp('/')

    expect(await screen.findByRole('heading', { name: 'Home' })).toBeInTheDocument()
    expect(screen.getByText('Dieses Modul ist in Arbeit.')).toBeInTheDocument()
  })

  it('renders the page of an enabled module with its breadcrumb', async () => {
    renderApp('/noten')

    expect(await screen.findByRole('heading', { level: 1, name: 'Notendurchschnitte' })).toBeInTheDocument()
    const breadcrumb = screen.getByRole('navigation', { name: 'Breadcrumb' })
    expect(within(breadcrumb).getAllByRole('listitem').map((item) => item.textContent)).toEqual(['Home', 'Noten'])
  })

  it('shows the disabled-module card instead of the page when the module is disabled', async () => {
    server.use(http.get('/api/modules', () => HttpResponse.json(modulesWithDisabled('grades'))))

    renderApp('/noten')

    expect(await screen.findByText('Dieses Modul ist zurzeit deaktiviert.')).toBeInTheDocument()
    expect(screen.queryByText('Dieses Modul ist in Arbeit.')).not.toBeInTheDocument()
  })

  it('shows the combined page when only one of classes and courses is enabled', async () => {
    server.use(http.get('/api/modules', () => HttpResponse.json(modulesWithDisabled('courses'))))

    renderApp('/klassen-kurse')

    expect(await screen.findByRole('heading', { name: 'Klassen & Kurse' })).toBeInTheDocument()
    expect(screen.queryByText('Dieses Modul ist zurzeit deaktiviert.')).not.toBeInTheDocument()
  })

  it('shows the not-found page for an unknown path', async () => {
    renderApp('/gibt-es-nicht')

    expect(await screen.findByRole('heading', { name: 'Seite nicht gefunden' })).toBeInTheDocument()
    expect(screen.getByRole('link', { name: 'Zur Startseite' })).toHaveAttribute('href', '/')
  })

  it('navigates between modules via the main navigation', async () => {
    const user = userEvent.setup()
    renderApp('/')

    await user.click(await screen.findByRole('link', { name: 'Stundenplan' }))

    expect(await screen.findByRole('heading', { name: 'Stundenplan' })).toBeInTheDocument()
    expect(screen.getByRole('link', { name: 'Stundenplan' })).toHaveAttribute('aria-current', 'page')
  })

  describe('when a startup request fails', () => {
    beforeEach(() => {
      vi.spyOn(console, 'error').mockImplementation(() => {})
    })

    afterEach(() => {
      vi.restoreAllMocks()
    })

    it('shows an error card instead of crashing', async () => {
      server.use(
        http.get('/api/modules', () =>
          HttpResponse.json(
            { title: 'Service Unavailable', status: 503, detail: 'Backend nicht erreichbar.' },
            { status: 503 },
          ),
        ),
      )

      renderApp()

      expect(await screen.findByRole('alert')).toBeInTheDocument()
      expect(screen.getByText('Die Anwendung konnte nicht geladen werden.')).toBeInTheDocument()
      expect(screen.getByText('Backend nicht erreichbar.')).toBeInTheDocument()
      expect(screen.queryByRole('navigation', { name: 'Hauptnavigation' })).not.toBeInTheDocument()
    })

    it('still shows the top bar with the product name', async () => {
      server.use(http.get('/api/me', () => new HttpResponse(null, { status: 500 })))

      renderApp()

      expect(await screen.findByRole('alert')).toBeInTheDocument()
      expect(screen.getByRole('link', { name: 'BetterIntranet' })).toBeInTheDocument()
    })
  })
})
