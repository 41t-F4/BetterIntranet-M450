import { screen, within } from '@testing-library/react'
import userEvent from '@testing-library/user-event'
import { HttpResponse, http } from 'msw'
import { describe, expect, it, vi } from 'vitest'
import { AppDataProvider } from '../../app/providers/AppDataProvider'
import { myCoursesFixture } from '../../test/fixtures/courses'
import { modulesWithDisabled } from '../../test/fixtures/modules'
import { server } from '../../test/msw/server'
import { renderWithRouter } from '../../test/render'
import { ClassesCoursesPage } from './ClassesCoursesPage'

const DISABLED_TEXT = 'Dieses Modul ist zurzeit deaktiviert.'

function renderPage() {
  return renderWithRouter(
    <AppDataProvider>
      <ClassesCoursesPage />
    </AppDataProvider>,
    { path: '/klassen-kurse' },
  )
}

function classesCard() {
  return screen.getByRole('region', { name: 'Meine Klassen' })
}

function coursesCard() {
  return screen.getByRole('region', { name: 'Meine Kurse' })
}

/** Rows with data cells (i.e. without the header row) inside the given element. */
function dataRows(container: HTMLElement) {
  return within(container)
    .getAllByRole('row')
    .filter((row) => within(row).queryAllByRole('cell').length > 0)
}

function cellTexts(row: HTMLElement) {
  return within(row)
    .getAllByRole('cell')
    .map((cell) => cell.textContent)
}

function columnHeaders(table: HTMLElement) {
  return within(table)
    .getAllByRole('columnheader')
    .map((header) => header.textContent)
}

function problem(status: number, title: string, detail: string, extra: Record<string, string> = {}) {
  return HttpResponse.json({ type: 'about:blank', title, status, detail, ...extra }, { status })
}

describe('ClassesCoursesPage', () => {
  it('renders the page title and both cards', async () => {
    renderPage()

    expect(screen.getByRole('heading', { level: 1, name: 'Klassen & Kurse' })).toBeInTheDocument()
    expect(screen.getByRole('heading', { level: 2, name: 'Meine Klassen' })).toBeInTheDocument()
    expect(screen.getByRole('heading', { level: 2, name: 'Meine Kurse' })).toBeInTheDocument()
    await screen.findByRole('row', { name: /AP 24b/ })
  })

  it('shows a loading indicator per card until the data arrives', async () => {
    renderPage()

    const statuses = screen.getAllByRole('status')
    expect(statuses).toHaveLength(2)
    expect(within(classesCard()).getByRole('status')).toHaveTextContent('Laden')
    expect(within(coursesCard()).getByRole('status')).toHaveTextContent('Laden')

    await screen.findByRole('row', { name: /AP 24b/ })
    await screen.findByRole('row', { name: /Applikationen testen/ })
    expect(screen.queryByRole('status')).not.toBeInTheDocument()
  })

  describe('«Meine Klassen»', () => {
    it('lists the classes of the user with the role in German', async () => {
      renderPage()

      const row = await screen.findByRole('row', { name: /AP 24b/ })

      expect(cellTexts(row)).toEqual([
        'AP 24b',
        'Informatiker/innen Applikationsentwicklung',
        'IT',
        'MID',
        '2026/27',
        'Lernende/r',
      ])
    })

    it('has the expected columns', async () => {
      renderPage()
      await screen.findByRole('row', { name: /AP 24b/ })

      const table = within(classesCard()).getByRole('table')

      expect(columnHeaders(table)).toEqual(['Klasse', 'Beruf', 'Abteilung', 'Klassenlehrperson', 'Schuljahr', 'Rolle'])
    })

    it('shows an empty state when the user has no classes', async () => {
      server.use(http.get('/api/classes', () => HttpResponse.json([])))

      renderPage()

      expect(await within(classesCard()).findByText('Keine Klassen gefunden.')).toBeInTheDocument()
      expect(within(classesCard()).queryByRole('table')).not.toBeInTheDocument()
    })

    it('shows the problem detail as alert when the request fails', async () => {
      server.use(http.get('/api/classes', () => problem(404, 'Not Found', 'Benutzer unbekannt.')))

      renderPage()

      const alert = await within(classesCard()).findByRole('alert')

      expect(alert).toHaveTextContent('Benutzer unbekannt.')
      expect(within(coursesCard()).queryByRole('alert')).not.toBeInTheDocument()
    })

    it('shows a generic error when the response carries no problem detail', async () => {
      server.use(http.get('/api/classes', () => new HttpResponse(null, { status: 500 })))

      renderPage()

      expect(await within(classesCard()).findByRole('alert')).toHaveTextContent(
        'Die Daten konnten nicht geladen werden.',
      )
    })

    it('shows the disabled text without calling /api/classes when the module is disabled', async () => {
      const classesRequested = vi.fn()
      server.use(
        http.get('/api/modules', () => HttpResponse.json(modulesWithDisabled('classes'))),
        http.get('/api/classes', () => {
          classesRequested()
          return HttpResponse.json([])
        }),
      )

      renderPage()

      expect(await within(classesCard()).findByText(DISABLED_TEXT)).toBeInTheDocument()
      expect(await screen.findByRole('row', { name: /Applikationen testen/ })).toBeInTheDocument()
      expect(classesRequested).not.toHaveBeenCalled()
      expect(within(classesCard()).queryByRole('alert')).not.toBeInTheDocument()
    })

    it('shows the disabled text when the backend answers 503 for the classes module', async () => {
      server.use(
        http.get('/api/classes', () =>
          problem(503, 'Service Unavailable', 'Modul classes ist deaktiviert.', { module: 'classes' }),
        ),
      )

      renderPage()

      expect(await within(classesCard()).findByText(DISABLED_TEXT)).toBeInTheDocument()
      expect(await screen.findByRole('row', { name: /Applikationen testen/ })).toBeInTheDocument()
      expect(within(classesCard()).queryByRole('alert')).not.toBeInTheDocument()
    })
  })

  describe('«Meine Kurse»', () => {
    it('shows the courses of the newest semester in one table with a course count by default', async () => {
      renderPage()
      await screen.findByRole('row', { name: /Applikationen testen/ })

      const card = coursesCard()

      expect(screen.getByRole('combobox', { name: 'Semester' })).toHaveValue('HS 2026')
      expect(within(card).getAllByRole('table')).toHaveLength(1)
      expect(within(card).queryAllByRole('heading', { level: 3 })).toHaveLength(0)
      expect(within(card).getByText('4 Kurse')).toBeInTheDocument()
      expect(dataRows(card).map((row) => cellTexts(row)[0])).toEqual(['306', '450', 'ENG', 'GES'])
    })

    it('renders the expected columns and values', async () => {
      renderPage()
      const row = await screen.findByRole('row', { name: /Applikationen testen/ })

      expect(columnHeaders(within(coursesCard()).getByRole('table'))).toEqual([
        'Modul/Fach',
        'Bezeichnung',
        'Lehrperson',
        'Semester',
        'Lektionen/Woche',
        'Raum',
      ])
      expect(cellTexts(row)).toEqual(['450', 'Applikationen testen', 'VAJ', 'HS 2026', '4', 'AU-060'])
      expect(cellTexts(screen.getByRole('row', { name: /Englisch/ }))).toEqual([
        'ENG',
        'Englisch',
        'MID',
        'HS 2026',
        '2',
        'AU-512',
      ])
    })

    it('offers «Alle» and the distinct semesters, newest first, with the newest selected', async () => {
      renderPage()
      await screen.findByRole('row', { name: /Applikationen testen/ })

      const select = screen.getByRole('combobox', { name: 'Semester' })

      expect(within(select).getAllByRole('option').map((option) => option.textContent)).toEqual([
        'Alle',
        'HS 2026',
        'FS 2026',
      ])
      expect(screen.getByRole('option', { name: 'HS 2026' })).toHaveProperty('selected', true)
    })

    it('filters the courses client-side by the selected semester', async () => {
      const coursesRequested = vi.fn()
      server.use(
        http.get('/api/courses', () => {
          coursesRequested()
          return HttpResponse.json(myCoursesFixture)
        }),
      )
      const user = userEvent.setup()
      renderPage()
      await screen.findByRole('row', { name: /Applikationen testen/ })

      await user.selectOptions(screen.getByRole('combobox', { name: 'Semester' }), 'FS 2026')

      const card = coursesCard()
      expect(dataRows(card).map((row) => cellTexts(row)[0])).toEqual(['320', '322', 'ENG', 'GES'])
      expect(dataRows(card).every((row) => cellTexts(row)[3] === 'FS 2026')).toBe(true)
      expect(within(card).getByText('4 Kurse')).toBeInTheDocument()
      expect(screen.queryByRole('row', { name: /Applikationen testen/ })).not.toBeInTheDocument()
      expect(coursesRequested).toHaveBeenCalledTimes(1)
    })

    it('groups the rows by semester with a sub-heading when «Alle» is selected', async () => {
      const user = userEvent.setup()
      renderPage()
      await screen.findByRole('row', { name: /Applikationen testen/ })

      await user.selectOptions(screen.getByRole('combobox', { name: 'Semester' }), 'Alle')

      const card = coursesCard()
      const groups = within(card).getAllByRole('region')
      expect(groups.map((group) => within(group).getByRole('heading', { level: 3 }).textContent)).toEqual([
        'HS 2026',
        'FS 2026',
      ])
      expect(within(card).getByText('8 Kurse')).toBeInTheDocument()
      expect(dataRows(card)).toHaveLength(8)
      expect(dataRows(within(card).getByRole('region', { name: 'HS 2026' })).map((row) => cellTexts(row)[1])).toEqual([
        'IT-Kleinprojekt abwickeln',
        'Applikationen testen',
        'Englisch',
        'Allgemeinbildung Gesellschaft',
      ])
      expect(dataRows(within(card).getByRole('region', { name: 'FS 2026' })).map((row) => cellTexts(row)[1])).toEqual([
        'Objektorientiert programmieren',
        'Benutzerschnittstellen entwerfen und implementieren',
        'Englisch',
        'Allgemeinbildung Gesellschaft',
      ])
    })

    it('shows an empty state when the user has no courses', async () => {
      server.use(http.get('/api/courses', () => HttpResponse.json([])))

      renderPage()

      expect(await within(coursesCard()).findByText('Keine Kurse gefunden.')).toBeInTheDocument()
      expect(within(coursesCard()).queryByRole('table')).not.toBeInTheDocument()
    })

    it('shows the problem detail as alert on 502 while the classes still render', async () => {
      server.use(
        http.get('/api/courses', () =>
          problem(502, 'Bad Gateway', 'Schulsystem nicht erreichbar.', { upstream: 'school' }),
        ),
      )

      renderPage()

      expect(await within(coursesCard()).findByRole('alert')).toHaveTextContent('Schulsystem nicht erreichbar.')
      expect(await screen.findByRole('row', { name: /AP 24b/ })).toBeInTheDocument()
      expect(within(classesCard()).queryByRole('alert')).not.toBeInTheDocument()
      expect(screen.queryByRole('combobox', { name: 'Semester' })).not.toBeInTheDocument()
    })

    it('shows the disabled text without calling /api/courses when the module is disabled', async () => {
      const coursesRequested = vi.fn()
      server.use(
        http.get('/api/modules', () => HttpResponse.json(modulesWithDisabled('courses'))),
        http.get('/api/courses', () => {
          coursesRequested()
          return HttpResponse.json([])
        }),
      )

      renderPage()

      expect(await within(coursesCard()).findByText(DISABLED_TEXT)).toBeInTheDocument()
      expect(await screen.findByRole('row', { name: /AP 24b/ })).toBeInTheDocument()
      expect(coursesRequested).not.toHaveBeenCalled()
      expect(screen.queryByRole('combobox', { name: 'Semester' })).not.toBeInTheDocument()
    })
  })
})
