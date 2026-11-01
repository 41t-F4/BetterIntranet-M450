import { screen, waitFor, within } from '@testing-library/react'
import userEvent from '@testing-library/user-event'
import { HttpResponse, http } from 'msw'
import { describe, expect, it } from 'vitest'
import { absencesResponseFixture, absencesResponseForStatus, emptyAbsencesResponse } from '../../test/fixtures/absences'
import { server } from '../../test/msw/server'
import { renderWithRouter } from '../../test/render'
import { AbsencesPage } from './AbsencesPage'
import { isAbsenceStatus } from './lib/status'
import type { AbsencesResponse } from './types'

const HINT = 'Offene Absenzen müssen innert 14 Tagen entschuldigt werden.'

function renderPage() {
  return renderWithRouter(<AbsencesPage />, { path: '/absenzen' })
}

/** The summary card, i.e. the card headed by the page title. */
function summaryCard(): HTMLElement {
  const card = screen.getByRole('heading', { level: 1, name: 'Absenzen' }).closest('section')
  if (!card) throw new Error('summary card not found')
  return card
}

/** Value of a summary tile, looked up by its label (`<dt>`). */
function tileValue(label: string): string | null | undefined {
  return within(summaryCard()).getByText(label, { selector: 'dt' }).nextElementSibling?.textContent
}

function dataRows(): HTMLElement[] {
  return within(screen.getByRole('table')).getAllByRole('row').slice(1)
}

function respondWith(response: AbsencesResponse) {
  server.use(http.get('/api/absences', () => HttpResponse.json(response)))
}

/** Answers like the default handler but records every requested URL. */
function recordRequests(): string[] {
  const requestedUrls: string[] = []
  server.use(
    http.get('/api/absences', ({ request }) => {
      requestedUrls.push(request.url)
      const status = new URL(request.url).searchParams.get('status')
      return HttpResponse.json(absencesResponseForStatus(isAbsenceStatus(status) ? status : null))
    }),
  )
  return requestedUrls
}

describe('AbsencesPage', () => {
  describe('loading', () => {
    it('announces the loading state', () => {
      renderPage()

      expect(screen.getByRole('status')).toHaveTextContent('Absenzen werden geladen')
    })

    it('removes the loading state once the data is shown', async () => {
      renderPage()

      await screen.findByRole('table')

      expect(screen.queryByRole('status')).not.toBeInTheDocument()
    })
  })

  describe('summary tiles', () => {
    it('show the counts of all absences and lessons', async () => {
      renderPage()
      await screen.findByRole('table')

      expect(tileValue('Total')).toBe('7')
      expect(tileValue('Offen')).toBe('2')
      expect(tileValue('Entschuldigt')).toBe('3')
      expect(tileValue('Unentschuldigt')).toBe('2')
      expect(tileValue('Lektionen')).toBe('16')
    })
  })

  describe('entries table', () => {
    it('has the expected column headers', async () => {
      renderPage()
      const table = await screen.findByRole('table')

      const headers = within(table).getAllByRole('columnheader').map((header) => header.textContent)

      expect(headers).toEqual(['Datum', 'Zeit', 'Fach', 'Lehrperson', 'Status', 'Grund', 'Gemeldet am'])
    })

    it('lists one row per absence', async () => {
      renderPage()
      await screen.findByRole('table')

      expect(dataRows()).toHaveLength(7)
    })

    it('shows subject, teacher, formatted date, time and reported-at of an absence', async () => {
      renderPage()
      await screen.findByRole('table')

      const row = dataRows()[0]!
      expect(within(row).getByText('27.10.2026')).toBeInTheDocument()
      expect(within(row).getByText('08:10–09:45')).toBeInTheDocument()
      expect(within(row).getByText('Englisch')).toBeInTheDocument()
      expect(within(row).getByText('MID')).toBeInTheDocument()
      expect(within(row).getByText('27.10.2026 10:00')).toBeInTheDocument()
    })

    it('shows the status as German badge label', async () => {
      renderPage()
      await screen.findByRole('table')

      const rows = dataRows()
      expect(within(rows[0]!).getByText('Offen')).toBeInTheDocument()
      expect(within(rows[2]!).getByText('Entschuldigt')).toBeInTheDocument()
      expect(within(rows[5]!).getByText('Unentschuldigt')).toBeInTheDocument()
    })

    it('shows the reason, or a dash when there is none', async () => {
      renderPage()
      await screen.findByRole('table')

      const rows = dataRows()
      expect(within(rows[2]!).getByText('Arzttermin')).toBeInTheDocument()
      expect(within(rows[0]!).getByText('–')).toBeInTheDocument()
    })
  })

  describe('status filter', () => {
    it('offers all statuses plus «Alle»', async () => {
      renderPage()
      const select = await screen.findByRole('combobox', { name: 'Status' })

      const options = within(select)
        .getAllByRole('option')
        .map((option) => option.textContent)

      expect(options).toEqual(['Alle', 'Offen', 'Entschuldigt', 'Unentschuldigt'])
    })

    it('refetches with ?status= and shows the narrowed list while the summary stays', async () => {
      const requestedUrls = recordRequests()
      const user = userEvent.setup()
      renderPage()
      await screen.findByRole('table')

      await user.selectOptions(screen.getByRole('combobox', { name: 'Status' }), 'EXCUSED')

      await waitFor(() => expect(requestedUrls.at(-1)).toMatch(/\/api\/absences\?status=EXCUSED$/))
      await waitFor(() => expect(dataRows()).toHaveLength(3))
      expect(screen.queryByText('Englisch')).not.toBeInTheDocument()
      expect(within(screen.getByRole('table')).getByText('Allgemeinbildung Gesellschaft')).toBeInTheDocument()
      expect(tileValue('Total')).toBe('7')
      expect(tileValue('Entschuldigt')).toBe('3')
    })

    it('requests the unfiltered list again when «Alle» is selected', async () => {
      const requestedUrls = recordRequests()
      const user = userEvent.setup()
      renderPage()
      await screen.findByRole('table')
      await user.selectOptions(screen.getByRole('combobox', { name: 'Status' }), 'OPEN')
      await waitFor(() => expect(dataRows()).toHaveLength(2))

      await user.selectOptions(screen.getByRole('combobox', { name: 'Status' }), '')

      await waitFor(() => expect(dataRows()).toHaveLength(7))
      expect(requestedUrls.at(-1)).toMatch(/\/api\/absences$/)
    })
  })

  describe('hint for open absences', () => {
    it('is shown when there are open absences', async () => {
      renderPage()
      await screen.findByRole('table')

      expect(screen.getByText(HINT)).toBeInTheDocument()
    })

    it('is hidden when no absence is open', async () => {
      respondWith({
        summary: { ...absencesResponseFixture.summary, open: 0 },
        absences: absencesResponseFixture.absences.filter((absence) => absence.status !== 'OPEN'),
      })
      renderPage()
      await screen.findByRole('table')

      expect(screen.queryByText(HINT)).not.toBeInTheDocument()
    })
  })

  describe('empty state', () => {
    it('says that there are no absences instead of showing a table', async () => {
      respondWith(emptyAbsencesResponse)
      renderPage()

      expect(await screen.findByText('Keine Absenzen vorhanden.')).toBeInTheDocument()

      expect(screen.queryByRole('table')).not.toBeInTheDocument()
      expect(tileValue('Total')).toBe('0')
      expect(screen.queryByText(HINT)).not.toBeInTheDocument()
    })
  })

  describe('error state', () => {
    it('shows an alert with the problem detail when the school system is unreachable', async () => {
      server.use(
        http.get('/api/absences', () =>
          HttpResponse.json(
            { title: 'Bad Gateway', status: 502, detail: 'Schulsystem nicht erreichbar.', upstream: 'school' },
            { status: 502 },
          ),
        ),
      )
      renderPage()

      const alert = await screen.findByRole('alert')

      expect(alert).toHaveTextContent('Absenzen konnten nicht geladen werden.')
      expect(alert).toHaveTextContent('Schulsystem nicht erreichbar.')
      expect(screen.queryByRole('status')).not.toBeInTheDocument()
      expect(screen.queryByRole('table')).not.toBeInTheDocument()
    })

    it('falls back to the error title when the problem has no detail', async () => {
      server.use(http.get('/api/absences', () => HttpResponse.json({ title: 'Not Found', status: 404 }, { status: 404 })))
      renderPage()

      const alert = await screen.findByRole('alert')

      expect(alert).toHaveTextContent('Not Found')
    })

    it('keeps the status filter usable so the user can retry with another status', async () => {
      server.use(
        http.get('/api/absences', () => HttpResponse.json({ title: 'Bad Gateway', status: 502 }, { status: 502 }), {
          once: true,
        }),
      )
      const user = userEvent.setup()
      renderPage()
      await screen.findByRole('alert')

      await user.selectOptions(screen.getByRole('combobox', { name: 'Status' }), 'OPEN')

      await waitFor(() => expect(dataRows()).toHaveLength(2))
      expect(screen.queryByRole('alert')).not.toBeInTheDocument()
    })
  })
})
