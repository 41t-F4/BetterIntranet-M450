import { render, screen, within } from '@testing-library/react'
import { HttpResponse, http } from 'msw'
import { describe, expect, it } from 'vitest'
import { emptyGradesOverviewFixture } from '../../test/fixtures/grades'
import { server } from '../../test/msw/server'
import { GradesPage } from './GradesPage'

const SCHOOL_UNAVAILABLE = {
  type: 'about:blank',
  title: 'Bad Gateway',
  status: 502,
  detail: 'Das Schulsystem ist zurzeit nicht erreichbar.',
  upstream: 'school',
}

async function renderLoadedPage() {
  render(<GradesPage />)
  await screen.findByRole('heading', { level: 1, name: 'Notendurchschnitte' })
}

function subjectCard(name: string) {
  return within(screen.getByRole('region', { name }))
}

function rowCells(table: HTMLElement): string[][] {
  return within(table)
    .getAllByRole('row')
    .slice(1)
    .map((row) =>
      [...within(row).queryAllByRole('rowheader'), ...within(row).getAllByRole('cell')].map(
        (cell) => cell.textContent ?? '',
      ),
    )
}

describe('GradesPage', () => {
  it('shows a loading status while the grades are being fetched', () => {
    render(<GradesPage />)

    expect(screen.getByRole('status')).toHaveTextContent('Noten werden geladen')
  })

  describe('averages card', () => {
    it('shows the overall average next to the title', async () => {
      await renderLoadedPage()

      expect(screen.getByText('Gesamt')).toHaveTextContent('Gesamt 5.09')
      expect(screen.queryByRole('status')).not.toBeInTheDocument()
    })

    it('renders the bar chart of the averages', async () => {
      await renderLoadedPage()

      expect(screen.getByRole('img', { name: 'Balkendiagramm der Notendurchschnitte' })).toBeInTheDocument()
    })

    it('summarises average and report grade mean per school part in a table', async () => {
      await renderLoadedPage()

      const table = screen.getByRole('table', { name: 'Durchschnitte pro Schulteil' })

      expect(rowCells(table)).toEqual([
        ['BMS', '4.94', '5.0'],
        ['TBZ', '5.0', '5.0'],
        ['ÜK', '5.33', '5.5'],
      ])
    })
  })

  describe('school part sections', () => {
    it('renders one heading per school part with its average, in backend order', async () => {
      await renderLoadedPage()

      const headings = screen.getAllByRole('heading', { level: 2 }).map((heading) => heading.textContent)

      expect(headings).toEqual(['BMS 4.94', 'TBZ 5.0', 'ÜK 5.33'])
    })

    it('renders a card per subject with semester, average and report grade badge', async () => {
      await renderLoadedPage()

      const deutsch = subjectCard('Deutsch HS 2026')
      expect(deutsch.getByRole('heading', { level: 3, name: 'Deutsch' })).toBeInTheDocument()
      expect(deutsch.getByText('HS 2026')).toBeInTheDocument()
      expect(deutsch.getByText('5.88')).toBeInTheDocument()
      expect(deutsch.getByText('Zeugnis 6.0')).toBeInTheDocument()

      const uek = subjectCard('ÜK 294 Frontend-Applikation FS 2026')
      expect(uek.getByText('5.33')).toBeInTheDocument()
      expect(uek.getByText('Zeugnis 5.5')).toBeInTheDocument()
    })

    it('lists the grades of a subject with title, Swiss date, grade and weight', async () => {
      await renderLoadedPage()

      const table = subjectCard('Deutsch HS 2026').getByRole('table')

      expect(within(table).getAllByRole('columnheader').map((cell) => cell.textContent)).toEqual([
        'Titel',
        'Datum',
        'Note',
        'Gewichtung',
      ])
      expect(rowCells(table)).toEqual([
        ['Aufsatz Erörterung', '21.10.2026', '6.0', '2.0'],
        ['Vortrag Literatur', '30.09.2026', '6.0', '1.0'],
        ['Grammatiktest', '09.09.2026', '5.5', '1.0'],
      ])
    })

    it('marks only grades below 4.0 as insufficient', async () => {
      await renderLoadedPage()

      const insufficient = screen.getAllByLabelText('ungenügend')

      // Exactly one: the 3.5 grade. The Mathematik average of exactly 4.0 is sufficient.
      expect(insufficient).toHaveLength(1)
      expect(insufficient[0]).toHaveTextContent('3.5')
      expect(subjectCard('Mathematik HS 2026').getAllByLabelText('ungenügend')).toHaveLength(1)
    })

    it('renders a dash and no badge for a subject without average', async () => {
      await renderLoadedPage()

      const subject = subjectCard('Modul 320 Objektorientiert programmieren HS 2026')

      expect(subject.getByText('–')).toBeInTheDocument()
      expect(subject.queryByText(/Zeugnis/)).not.toBeInTheDocument()
      expect(subject.getByText('Noch keine Noten.')).toBeInTheDocument()
    })
  })

  describe('error and empty states', () => {
    it('shows the problem detail when the school system is unavailable', async () => {
      server.use(http.get('/api/grades', () => HttpResponse.json(SCHOOL_UNAVAILABLE, { status: 502 })))

      render(<GradesPage />)

      expect(await screen.findByRole('alert')).toHaveTextContent('Das Schulsystem ist zurzeit nicht erreichbar.')
      expect(screen.queryByRole('status')).not.toBeInTheDocument()
    })

    it('falls back to the error message when the response carries no detail', async () => {
      server.use(http.get('/api/grades', () => new HttpResponse(null, { status: 404 })))

      render(<GradesPage />)

      expect(await screen.findByRole('alert')).toHaveTextContent('404')
    })

    it('shows an empty state when the user has no grades yet', async () => {
      server.use(http.get('/api/grades', () => HttpResponse.json(emptyGradesOverviewFixture)))

      render(<GradesPage />)

      expect(await screen.findByText('Noch keine Noten vorhanden.')).toBeInTheDocument()
      expect(screen.queryByRole('img')).not.toBeInTheDocument()
      expect(screen.queryByRole('heading', { level: 2 })).not.toBeInTheDocument()
    })
  })
})
