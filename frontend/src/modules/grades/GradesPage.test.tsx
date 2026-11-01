import { render, screen, within } from '@testing-library/react'
import userEvent from '@testing-library/user-event'
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

      expect(screen.getByText('Gesamt')).toHaveTextContent('Gesamt 5.02')
      expect(screen.queryByRole('status')).not.toBeInTheDocument()
    })

    it('renders the bar chart of the averages', async () => {
      await renderLoadedPage()

      expect(screen.getByRole('img', { name: 'Balkendiagramm der Notendurchschnitte' })).toBeInTheDocument()
    })

    it('offers the semesters newest first and selects the newest one by default', async () => {
      await renderLoadedPage()

      const select = screen.getByRole<HTMLSelectElement>('combobox', { name: 'Semester' })

      expect(within(select).getAllByRole('option').map((option) => option.textContent)).toEqual([
        'HS 2026',
        'FS 2026',
      ])
      expect(select.value).toBe('HS 2026')
    })

    it('summarises average and report grade per subject of the selected semester with short labels', async () => {
      await renderLoadedPage()

      const table = screen.getByRole('table', { name: 'Durchschnitte pro Fach' })

      expect(within(table).getAllByRole('columnheader').map((cell) => cell.textContent)).toEqual([
        'Fach',
        'Durchschnitt',
        'Zeugnis',
      ])
      expect(rowCells(table)).toEqual([
        ['M450', '5.0', '5.0'],
        ['ABU', '4.0', '4.0'],
        ['Englisch', '5.88', '6.0'],
      ])
      expect(within(table).getByTitle('450 Applikationen testen')).toHaveTextContent('M450')
      expect(within(table).getByTitle('Allgemeinbildung Gesellschaft')).toHaveTextContent('ABU')
    })

    it('shows the subjects of another semester after selecting it', async () => {
      const user = userEvent.setup()
      await renderLoadedPage()

      await user.selectOptions(screen.getByRole('combobox', { name: 'Semester' }), 'FS 2026')

      expect(rowCells(screen.getByRole('table', { name: 'Durchschnitte pro Fach' }))).toEqual([
        ['M320', '–', '–'],
        ['M322', '5.33', '5.5'],
        ['ABU', '4.75', '5.0'],
        ['Englisch', '5.17', '5.0'],
      ])
    })
  })

  describe('semester sections', () => {
    it('renders one heading per semester with the mean of its subject averages, newest first', async () => {
      await renderLoadedPage()

      const headings = screen.getAllByRole('heading', { level: 2 }).map((heading) => heading.textContent)

      expect(headings).toEqual(['HS 2026 · Ø 4.96', 'FS 2026 · Ø 5.08'])
    })

    it('groups the subject cards by semester', async () => {
      await renderLoadedPage()

      const subjectsOf = (semester: string) =>
        within(screen.getByRole('region', { name: new RegExp(`^${semester}`) }))
          .getAllByRole('heading', { level: 3 })
          .map((heading) => heading.textContent)

      expect(subjectsOf('HS 2026')).toEqual(['450 Applikationen testen', 'Allgemeinbildung Gesellschaft', 'Englisch'])
      expect(subjectsOf('FS 2026')).toEqual([
        '320 Objektorientiert programmieren',
        '322 Benutzerschnittstellen entwerfen und implementieren',
        'Allgemeinbildung Gesellschaft',
        'Englisch',
      ])
    })

    it('mentions no school parts such as BMS or ÜK anywhere on the page', async () => {
      await renderLoadedPage()

      expect(document.body).not.toHaveTextContent(/BMS|ÜK|UEK|Schulteil/)
    })

    it('renders a card per subject with semester, average and report grade badge', async () => {
      await renderLoadedPage()

      const english = subjectCard('Englisch HS 2026')
      expect(english.getByRole('heading', { level: 3, name: 'Englisch' })).toBeInTheDocument()
      expect(english.getByText('HS 2026')).toBeInTheDocument()
      expect(english.getByText('5.88')).toBeInTheDocument()
      expect(english.getByText('Zeugnis 6.0')).toBeInTheDocument()

      const ui = subjectCard('322 Benutzerschnittstellen entwerfen und implementieren FS 2026')
      expect(ui.getByText('5.33')).toBeInTheDocument()
      expect(ui.getByText('Zeugnis 5.5')).toBeInTheDocument()
    })

    it('lists the grades of a subject with title, Swiss date, grade and weight', async () => {
      await renderLoadedPage()

      const table = subjectCard('450 Applikationen testen HS 2026').getByRole('table')

      expect(within(table).getAllByRole('columnheader').map((cell) => cell.textContent)).toEqual([
        'Titel',
        'Datum',
        'Note',
        'Gewichtung',
      ])
      expect(rowCells(table)).toEqual([
        ['LB2 Projektarbeit', '27.10.2026', '5.0', '2.0'],
        ['LB1 Testkonzept', '05.10.2026', '4.5', '1.0'],
        ['Kurztest Testarten', '15.09.2026', '5.5', '1.0'],
      ])
    })

    it('marks only grades below 4.0 as insufficient', async () => {
      await renderLoadedPage()

      const insufficient = screen.getAllByLabelText('ungenügend')

      // Exactly one: the 3.5 grade. The ABU average of exactly 4.0 is sufficient.
      expect(insufficient).toHaveLength(1)
      expect(insufficient[0]).toHaveTextContent('3.5')
      expect(subjectCard('Allgemeinbildung Gesellschaft HS 2026').getAllByLabelText('ungenügend')).toHaveLength(1)
    })

    it('renders a dash and no badge for a subject without average', async () => {
      await renderLoadedPage()

      const subject = subjectCard('320 Objektorientiert programmieren FS 2026')

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
      expect(screen.queryByRole('combobox', { name: 'Semester' })).not.toBeInTheDocument()
    })
  })
})
