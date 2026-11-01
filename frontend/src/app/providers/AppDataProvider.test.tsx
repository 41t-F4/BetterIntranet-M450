import { render, screen } from '@testing-library/react'
import { HttpResponse, http } from 'msw'
import { afterEach, beforeEach, describe, expect, it, vi } from 'vitest'
import { modulesWithDisabled } from '../../test/fixtures/modules'
import { server } from '../../test/msw/server'
import { AppDataProvider } from './AppDataProvider'
import { useAppData, useCurrentUser, useModules } from './useAppData'

function Probe() {
  const { status, error } = useAppData()
  const user = useCurrentUser()
  const { isModuleEnabled } = useModules()
  return (
    <dl>
      <dt>Status</dt>
      <dd>{status}</dd>
      <dt>Fehler</dt>
      <dd>{error?.message ?? '-'}</dd>
      <dt>Benutzer</dt>
      <dd>{user?.username ?? '-'}</dd>
      <dt>Modul Noten</dt>
      <dd>{isModuleEnabled('grades') ? 'enabled' : 'disabled'}</dd>
    </dl>
  )
}

function renderProbe() {
  return render(
    <AppDataProvider>
      <Probe />
    </AppDataProvider>,
  )
}

describe('AppDataProvider', () => {
  it('starts in the loading state without user data', () => {
    renderProbe()

    expect(screen.getByText('loading')).toBeInTheDocument()
    expect(screen.getAllByText('-')).toHaveLength(2)
  })

  it('provides the user and module statuses once both requests succeeded', async () => {
    renderProbe()

    expect(await screen.findByText('ready')).toBeInTheDocument()
    expect(screen.getByText('nicola.wehrli')).toBeInTheDocument()
    expect(screen.getByText('enabled')).toBeInTheDocument()
  })

  it('reports a module as disabled according to the backend', async () => {
    server.use(http.get('/api/modules', () => HttpResponse.json(modulesWithDisabled('grades'))))

    renderProbe()

    expect(await screen.findByText('ready')).toBeInTheDocument()
    expect(screen.getByText('disabled')).toBeInTheDocument()
  })

  it('reports every module as disabled while loading', () => {
    renderProbe()

    expect(screen.getByText('disabled')).toBeInTheDocument()
  })

  it('exposes the error when a startup request fails', async () => {
    server.use(
      http.get('/api/me', () =>
        HttpResponse.json({ title: 'Service Unavailable', status: 503, detail: 'Backend down' }, { status: 503 }),
      ),
    )

    renderProbe()

    expect(await screen.findByText('error')).toBeInTheDocument()
    expect(screen.getByText('503 Service Unavailable')).toBeInTheDocument()
  })

  it('fails when /api/modules does not return a list', async () => {
    server.use(http.get('/api/modules', () => HttpResponse.json('not an array')))

    renderProbe()

    expect(await screen.findByText('error')).toBeInTheDocument()
    expect(screen.getByText('/api/modules', { exact: false })).toBeInTheDocument()
  })

  describe('hooks outside of the provider', () => {
    beforeEach(() => {
      vi.spyOn(console, 'error').mockImplementation(() => {})
    })

    afterEach(() => {
      vi.restoreAllMocks()
    })

    it('throw a descriptive error', () => {
      expect(() => render(<Probe />)).toThrow('AppDataProvider')
    })
  })
})
