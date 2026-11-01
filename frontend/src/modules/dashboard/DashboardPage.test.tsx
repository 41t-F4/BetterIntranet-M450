import { render, screen, within } from '@testing-library/react'
import { HttpResponse, http } from 'msw'
import { MemoryRouter } from 'react-router-dom'
import { describe, expect, it } from 'vitest'
import { AppDataContext, type AppData } from '../../app/providers/useAppData'
import type { CurrentUser } from '../../shared/api/types'
import { currentUserFixture } from '../../test/fixtures/currentUser'
import { dashboardFixture } from '../../test/fixtures/dashboard'
import { server } from '../../test/msw/server'
import { DashboardPage } from './DashboardPage'

const AFTERNOON = new Date(2026, 10, 2, 14, 30)

function appData(user: CurrentUser | null): AppData {
  return {
    status: user ? 'ready' : 'loading',
    error: null,
    user,
    modules: [],
    isModuleEnabled: () => true,
  }
}

function renderPage({ now = AFTERNOON, user = currentUserFixture as CurrentUser | null } = {}) {
  return render(
    <AppDataContext.Provider value={appData(user)}>
      <MemoryRouter>
        <DashboardPage now={() => now} />
      </MemoryRouter>
    </AppDataContext.Provider>,
  )
}

describe('DashboardPage', () => {
  it('greets the logged-in user by first name', async () => {
    renderPage()

    expect(screen.getByRole('heading', { level: 1, name: 'Guten Tag, Nicola' })).toBeInTheDocument()
    await screen.findByRole('heading', { name: 'Newsticker' })
  })

  it('greets with «Guten Morgen» in the morning and «Guten Abend» in the evening', async () => {
    const { unmount } = renderPage({ now: new Date(2026, 10, 2, 10, 59) })
    expect(screen.getByRole('heading', { level: 1, name: 'Guten Morgen, Nicola' })).toBeInTheDocument()
    await screen.findByRole('heading', { name: 'Newsticker' })
    unmount()

    renderPage({ now: new Date(2026, 10, 2, 18, 0) })
    expect(screen.getByRole('heading', { level: 1, name: 'Guten Abend, Nicola' })).toBeInTheDocument()
    await screen.findByRole('heading', { name: 'Newsticker' })
  })

  it('shows a loading indicator until the dashboard is loaded', async () => {
    renderPage()

    expect(screen.getByRole('status')).toHaveTextContent('Dashboard wird geladen')
    await screen.findByRole('heading', { name: 'Newsticker' })
    expect(screen.queryByRole('status')).not.toBeInTheDocument()
  })

  it('shows all five cards', async () => {
    renderPage()

    for (const heading of ['Newsticker', 'Termine der Schule', 'Prävention', 'Meine Links', 'Quick Links']) {
      expect(await screen.findByRole('heading', { level: 2, name: heading })).toBeInTheDocument()
    }
  })

  it('shows the news of the backend', async () => {
    renderPage()

    expect(await screen.findByRole('heading', { name: 'Tag der offenen Tür 2026' })).toBeInTheDocument()
    expect(screen.getByText('28.10.2026')).toBeInTheDocument()
  })

  it('marks school holidays with «Ferien»', async () => {
    renderPage()

    const holiday = (await screen.findByText('Weihnachtsferien')).closest('li')
    expect(holiday).not.toBeNull()
    expect(within(holiday as HTMLElement).getByText('Ferien')).toBeInTheDocument()
    expect(within(holiday as HTMLElement).getByText('21.12.2026 – 02.01.2027')).toBeInTheDocument()
  })

  it('links «Noten» of «Meine Links» to the in-app grades page', async () => {
    renderPage()

    const myLinks = (await screen.findByRole('heading', { name: 'Meine Links' })).closest('section')
    expect(within(myLinks as HTMLElement).getByRole('link', { name: 'Noten' })).toHaveAttribute('href', '/noten')
  })

  it('opens external quick links in a new tab', async () => {
    renderPage()

    const link = await screen.findByRole('link', { name: /Service Desk Pro/ })
    expect(link).toHaveAttribute('href', 'https://servicedesk.tbz.ch')
    expect(link).toHaveAttribute('target', '_blank')
  })

  it('shows the empty-state messages when the backend has no news and no events', async () => {
    server.use(http.get('/api/dashboard', () => HttpResponse.json({ ...dashboardFixture, news: [], events: [] })))

    renderPage()

    expect(await screen.findByText('Zur Zeit liegen keine Nachrichten vor.')).toBeInTheDocument()
    expect(screen.getByText('Keine Kalendereinträge gefunden.')).toBeInTheDocument()
  })

  it('shows an error card with the ProblemDetail detail when the backend fails', async () => {
    server.use(
      http.get('/api/dashboard', () =>
        HttpResponse.json(
          { title: 'Internal Server Error', status: 500, detail: 'Die News konnten nicht gelesen werden.' },
          { status: 500 },
        ),
      ),
    )

    renderPage()

    const alert = await screen.findByRole('alert')
    expect(alert).toHaveTextContent('Das Dashboard konnte nicht geladen werden.')
    expect(alert).toHaveTextContent('Die News konnten nicht gelesen werden.')
    expect(screen.queryByRole('heading', { name: 'Newsticker' })).not.toBeInTheDocument()
    expect(screen.getByRole('heading', { level: 1, name: 'Guten Tag, Nicola' })).toBeInTheDocument()
  })

  it('falls back to a generic message when the error has no detail', async () => {
    server.use(http.get('/api/dashboard', () => new HttpResponse(null, { status: 502 })))

    renderPage()

    expect(await screen.findByRole('alert')).toHaveTextContent('Bitte versuche es später erneut.')
  })

  it('greets without a name while the user is not loaded', async () => {
    renderPage({ user: null })

    expect(screen.getByRole('heading', { level: 1, name: 'Guten Tag' })).toBeInTheDocument()
    await screen.findByRole('heading', { name: 'Newsticker' })
  })
})
