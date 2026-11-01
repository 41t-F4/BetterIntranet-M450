import { screen, waitFor, within } from '@testing-library/react'
import userEvent from '@testing-library/user-event'
import { HttpResponse, http } from 'msw'
import { useLocation } from 'react-router-dom'
import { afterEach, beforeEach, describe, expect, it, vi } from 'vitest'
import { AppDataProvider } from '../../app/providers/AppDataProvider'
import { server } from '../../test/msw/server'
import { renderWithRouter } from '../../test/render'
import { TimetablePage } from './TimetablePage'

/** Exposes the current URL so tests can assert the search params the page writes. */
function LocationProbe() {
  const { pathname, search } = useLocation()
  return <span data-testid="location">{pathname + search}</span>
}

function renderPage(search = '') {
  return renderWithRouter(
    <AppDataProvider>
      <TimetablePage />
      <LocationProbe />
    </AppDataProvider>,
    { path: `/stundenplan${search}` },
  )
}

function location() {
  return screen.getByTestId('location').textContent
}

async function findDay(name: string) {
  return screen.findByRole('region', { name })
}

/** Replaces the lessons handler with one that records every request URL and answers with `lessons`. */
function recordLessonRequests(lessons: unknown[] = []) {
  const urls: string[] = []
  server.use(
    http.get('/api/timetable', ({ request }) => {
      urls.push(request.url)
      return HttpResponse.json(lessons)
    }),
  )
  return urls
}

describe('TimetablePage', () => {
  beforeEach(() => {
    // Wednesday, 4. November 2026 (KW 45) – the fixture week.
    vi.useFakeTimers({ toFake: ['Date'] })
    vi.setSystemTime(new Date(2026, 10, 4, 10, 0))
  })

  afterEach(() => {
    vi.useRealTimers()
  })

  it('shows a loading indicator until the data arrived', () => {
    renderPage()

    expect(screen.getByRole('status')).toHaveTextContent('Laden')
  })

  it('renders the five weekdays Montag to Freitag of the current week as sections', async () => {
    renderPage()

    await findDay('Montag, 2. Nov.')
    const headings = screen.getAllByRole('heading', { level: 3 }).map((heading) => heading.textContent)
    expect(headings).toEqual([
      'Montag, 2. Nov.',
      'Dienstag, 3. Nov.',
      'Mittwoch, 4. Nov.',
      'Donnerstag, 5. Nov.',
      'Freitag, 6. Nov.',
    ])
    expect(screen.queryByRole('status')).not.toBeInTheDocument()
  })

  it('shows the week label of the current week', async () => {
    renderPage()

    expect(await screen.findByText('KW 45 · 2. Nov. – 6. Nov. 2026')).toBeInTheDocument()
  })

  it('preselects the class of the logged-in user in the class selector', async () => {
    renderPage()

    const select = await screen.findByRole('combobox', { name: 'Klasse' })
    await waitFor(() => expect(select).toHaveValue('7471'))
    expect(within(select).getAllByRole('option').map((option) => option.textContent)).toEqual([
      'AP 24a',
      'AP 24b',
      'PE 26a',
    ])
  })

  it('shows time range, subject, teacher and room of every lesson', async () => {
    renderPage()

    const tuesday = await findDay('Dienstag, 3. Nov.')
    expect(within(tuesday).getAllByRole('listitem')).toHaveLength(4)
    expect(within(tuesday).getByText('08:10 – 09:45')).toBeInTheDocument()
    expect(within(tuesday).getByText('ENG')).toHaveAttribute('title', 'Englisch')
    expect(within(tuesday).getByText('MID')).toBeInTheDocument()
    expect(within(tuesday).getByText('AU-512')).toBeInTheDocument()
    expect(within(tuesday).getByText('10:05 – 12:30')).toBeInTheDocument()
    expect(within(tuesday).getByText('GES')).toHaveAttribute('title', 'Allgemeinbildung Gesellschaft')
    expect(within(tuesday).getByText('RIA')).toBeInTheDocument()
    expect(within(tuesday).getByText('AU-202')).toBeInTheDocument()
    expect(within(tuesday).getByText('13:25 – 15:00')).toBeInTheDocument()
    expect(within(tuesday).getByText('15:20 – 16:55')).toBeInTheDocument()
    expect(within(tuesday).getAllByText('M450')).toHaveLength(2)
    expect(within(tuesday).getAllByText('M450')[0]).toHaveAttribute('title', '450 Applikationen testen')
    expect(within(tuesday).getAllByText('VAJ')).toHaveLength(2)
    expect(within(tuesday).getByText('AU-060')).toBeInTheDocument()
  })

  it('shows days without lessons as empty columns', async () => {
    renderPage()

    const monday = await findDay('Montag, 2. Nov.')
    expect(within(monday).queryAllByRole('listitem')).toHaveLength(0)
    expect(within(await findDay('Freitag, 6. Nov.')).queryAllByRole('listitem')).toHaveLength(0)
    expect(screen.queryByText('Keine Lektionen in dieser Woche.')).not.toBeInTheDocument()
  })

  it('shows the lesson info as additional text', async () => {
    renderPage()

    const tuesday = await findDay('Dienstag, 3. Nov.')
    expect(within(tuesday).getByText('oB')).toBeInTheDocument()
  })

  it('marks a cancelled lesson with «Ausfall»', async () => {
    renderPage()

    const tuesday = await findDay('Dienstag, 3. Nov.')
    const cancelled = within(tuesday).getByText('GES').closest('li')
    expect(cancelled).not.toBeNull()
    expect(within(cancelled as HTMLElement).getByText('Ausfall')).toBeInTheDocument()
    expect(within(tuesday).getByText('ENG').closest('li')).not.toHaveTextContent('Ausfall')
  })

  it('marks a changed lesson with the badge «Änderung»', async () => {
    renderPage()

    const tuesday = await findDay('Dienstag, 3. Nov.')
    const changed = within(tuesday).getByText('15:20 – 16:55').closest('li')
    expect(within(changed as HTMLElement).getByText('Änderung')).toBeInTheDocument()
    expect(within(changed as HTMLElement).getByText('Raumänderung')).toBeInTheDocument()
    expect(within(changed as HTMLElement).getByText('AU-057')).toBeInTheDocument()
    expect(within(tuesday).getByText('13:25 – 15:00').closest('li')).not.toHaveTextContent('Änderung')
  })

  it('shows the holiday name instead of the lessons on a holiday', async () => {
    renderPage()

    const thursday = await findDay('Donnerstag, 5. Nov.')
    expect(within(thursday).getByText('Tag der Schule')).toBeInTheDocument()
    expect(within(thursday).queryByText('ENG')).not.toBeInTheDocument()
    expect(within(thursday).queryAllByRole('listitem')).toHaveLength(0)
  })

  it('requests the lessons of the user class for Monday to Friday of the current week', async () => {
    const urls = recordLessonRequests()
    renderPage()

    await waitFor(() => expect(urls).toHaveLength(1))
    const params = new URL(urls[0] as string).searchParams
    expect(Object.fromEntries(params)).toEqual({ classId: '7471', from: '2026-11-02', to: '2026-11-06' })
  })

  it('refetches with the new class id and stores it in the URL when the class changes', async () => {
    const user = userEvent.setup()
    const urls = recordLessonRequests()
    renderPage()
    const select = await screen.findByRole('combobox', { name: 'Klasse' })
    await waitFor(() => expect(select).toHaveValue('7471'))

    await user.selectOptions(select, '7372')

    await waitFor(() => expect(urls.at(-1)).toContain('classId=7372'))
    expect(Object.fromEntries(new URL(urls.at(-1) as string).searchParams)).toEqual({
      classId: '7372',
      from: '2026-11-02',
      to: '2026-11-06',
    })
    expect(select).toHaveValue('7372')
    expect(location()).toContain('classId=7372')
  })

  it('navigates to the next week and stores it in the URL', async () => {
    const user = userEvent.setup()
    renderPage()
    await screen.findByText('KW 45 · 2. Nov. – 6. Nov. 2026')

    await user.click(screen.getByRole('button', { name: 'Nächste Woche' }))

    expect(await screen.findByText('KW 46 · 9. Nov. – 13. Nov. 2026')).toBeInTheDocument()
    expect(location()).toContain('week=2026-11-09')
    expect(await findDay('Montag, 9. Nov.')).toBeInTheDocument()
  })

  it('navigates to the previous week', async () => {
    const user = userEvent.setup()
    renderPage()
    await screen.findByText('KW 45 · 2. Nov. – 6. Nov. 2026')

    await user.click(screen.getByRole('button', { name: 'Vorherige Woche' }))

    expect(await screen.findByText('KW 44 · 26. Okt. – 30. Okt. 2026')).toBeInTheDocument()
    expect(location()).toContain('week=2026-10-26')
  })

  it('jumps back to the current week with «Heute»', async () => {
    const user = userEvent.setup()
    renderPage('?week=2026-11-16')
    await screen.findByText('KW 47 · 16. Nov. – 20. Nov. 2026')

    await user.click(screen.getByRole('button', { name: 'Heute' }))

    expect(await screen.findByText('KW 45 · 2. Nov. – 6. Nov. 2026')).toBeInTheDocument()
    expect(location()).toContain('week=2026-11-02')
  })

  it('requests the week from the URL when navigating', async () => {
    const user = userEvent.setup()
    const urls = recordLessonRequests()
    renderPage()
    await waitFor(() => expect(urls).toHaveLength(1))

    await user.click(screen.getByRole('button', { name: 'Nächste Woche' }))

    await waitFor(() => expect(urls).toHaveLength(2))
    expect(Object.fromEntries(new URL(urls[1] as string).searchParams)).toEqual({
      classId: '7471',
      from: '2026-11-09',
      to: '2026-11-13',
    })
  })

  it('reads class and week from the URL so the page is linkable', async () => {
    renderPage('?classId=7372&week=2026-11-09')

    expect(await screen.findByText('KW 46 · 9. Nov. – 13. Nov. 2026')).toBeInTheDocument()
    const select = screen.getByRole('combobox', { name: 'Klasse' })
    await waitFor(() => expect(select).toHaveValue('7372'))
  })

  it('normalizes a week param that is not a Monday to its week', async () => {
    renderPage('?week=2026-11-11')

    expect(await screen.findByText('KW 46 · 9. Nov. – 13. Nov. 2026')).toBeInTheDocument()
  })

  it('falls back to the current week and the user class for invalid params', async () => {
    renderPage('?week=gestern&classId=abc')

    expect(await screen.findByText('KW 45 · 2. Nov. – 6. Nov. 2026')).toBeInTheDocument()
    await waitFor(() => expect(screen.getByRole('combobox', { name: 'Klasse' })).toHaveValue('7471'))
  })

  it('shows a message when the week has no lessons', async () => {
    renderPage('?week=2026-11-09')

    expect(await screen.findByText('Keine Lektionen in dieser Woche.')).toBeInTheDocument()
    expect(screen.queryByRole('listitem')).not.toBeInTheDocument()
  })

  it('shows an error card when the timetable upstream is unreachable (502)', async () => {
    server.use(
      http.get('/api/timetable', () =>
        HttpResponse.json(
          {
            type: 'about:blank',
            title: 'Bad Gateway',
            status: 502,
            detail: 'stundenplan.geekz.ch hat nicht geantwortet.',
            upstream: 'timetable',
          },
          { status: 502 },
        ),
      ),
    )

    renderPage()

    const alert = await screen.findByRole('alert')
    expect(alert).toHaveTextContent('Der Stundenplan ist zurzeit nicht erreichbar.')
    expect(alert).toHaveTextContent('stundenplan.geekz.ch hat nicht geantwortet.')
    expect(screen.queryByRole('status')).not.toBeInTheDocument()
  })

  it('shows the problem detail of any other error', async () => {
    server.use(
      http.get('/api/timetable/holidays', () =>
        HttpResponse.json(
          { title: 'Internal Server Error', status: 500, detail: 'Ferien konnten nicht geladen werden.' },
          { status: 500 },
        ),
      ),
    )

    renderPage()

    const alert = await screen.findByRole('alert')
    expect(alert).toHaveTextContent('Der Stundenplan konnte nicht geladen werden.')
    expect(alert).toHaveTextContent('Ferien konnten nicht geladen werden.')
  })

  it('still offers the week navigation when the class list cannot be loaded', async () => {
    server.use(http.get('/api/timetable/classes', () => new HttpResponse(null, { status: 500 })))

    renderPage()

    expect(await screen.findByText('KW 45 · 2. Nov. – 6. Nov. 2026')).toBeInTheDocument()
    expect(screen.getByRole('button', { name: 'Nächste Woche' })).toBeInTheDocument()
    expect(await findDay('Montag, 2. Nov.')).toBeInTheDocument()
  })
})
