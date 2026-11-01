import { HttpResponse, http } from 'msw'
import type { AxiosAdapter } from 'axios'
import { describe, expect, it } from 'vitest'
import { server } from '../../test/msw/server'
import { ApiError, apiClient, apiGet } from './client'
import type { ProblemDetail } from './types'

describe('apiGet', () => {
  it('parses the JSON body of a successful response', async () => {
    server.use(http.get('/api/me', () => HttpResponse.json({ username: 'nicola.wehrli' })))

    const result = await apiGet<{ username: string }>('/api/me')

    expect(result).toEqual({ username: 'nicola.wehrli' })
  })

  it('sends an Accept header for JSON', async () => {
    let acceptHeader: string | null = null
    server.use(
      http.get('/api/me', ({ request }) => {
        acceptHeader = request.headers.get('accept')
        return HttpResponse.json({})
      }),
    )

    await apiGet('/api/me')

    expect(acceptHeader).toBe('application/json')
  })

  it('throws an ApiError with the fields of the ProblemDetail body on 404', async () => {
    const problem: ProblemDetail = {
      type: 'about:blank',
      title: 'Not Found',
      status: 404,
      detail: 'Klasse 999 existiert nicht.',
    }
    server.use(
      http.get('/api/classes/999', () =>
        HttpResponse.json(problem, { status: 404, headers: { 'Content-Type': 'application/problem+json' } }),
      ),
    )

    const error = await apiGet('/api/classes/999').catch((e: unknown) => e)

    expect(error).toBeInstanceOf(ApiError)
    const apiError = error as ApiError
    expect(apiError.status).toBe(404)
    expect(apiError.title).toBe('Not Found')
    expect(apiError.detail).toBe('Klasse 999 existiert nicht.')
    expect(apiError.module).toBeUndefined()
    expect(apiError.message).toBe('404 Not Found')
  })

  it('throws an ApiError carrying the module name on 503 (module disabled)', async () => {
    const problem: ProblemDetail = {
      type: 'https://betterintranet.tbz.ch/errors/module-disabled',
      title: 'Module disabled',
      status: 503,
      detail: 'Das Modul grades ist deaktiviert.',
      module: 'grades',
    }
    server.use(http.get('/api/grades', () => HttpResponse.json(problem, { status: 503 })))

    const error = await apiGet('/api/grades').catch((e: unknown) => e)

    expect(error).toBeInstanceOf(ApiError)
    const apiError = error as ApiError
    expect(apiError.status).toBe(503)
    expect(apiError.title).toBe('Module disabled')
    expect(apiError.detail).toBe('Das Modul grades ist deaktiviert.')
    expect(apiError.module).toBe('grades')
  })

  it('falls back to the status text when the error body is not JSON', async () => {
    server.use(
      http.get(
        '/api/grades',
        () => new HttpResponse('<html>Bad Gateway</html>', { status: 502, statusText: 'Bad Gateway' }),
      ),
    )

    const error = await apiGet('/api/grades').catch((e: unknown) => e)

    expect(error).toBeInstanceOf(ApiError)
    const apiError = error as ApiError
    expect(apiError.status).toBe(502)
    expect(apiError.title).toBe('Bad Gateway')
    expect(apiError.detail).toBeUndefined()
  })

  it('falls back to a generic title when the response has neither body nor status text', async () => {
    // MSW always fills in a status text, so this edge case is produced with a stub adapter.
    const bareResponse: AxiosAdapter = async (config) => {
      const { AxiosError } = await import('axios')
      const response = { data: '', status: 599, statusText: '', headers: {}, config }
      throw new AxiosError('Request failed', 'ERR_BAD_RESPONSE', config, null, response)
    }
    const previousAdapter = apiClient.defaults.adapter
    apiClient.defaults.adapter = bareResponse

    try {
      const error = await apiGet('/api/grades').catch((e: unknown) => e)

      expect(error).toBeInstanceOf(ApiError)
      expect((error as ApiError).title).toBe('HTTP 599')
    } finally {
      apiClient.defaults.adapter = previousAdapter
    }
  })

  it('sends query parameters through axios', async () => {
    let requestedUrl = ''
    server.use(
      http.get('/api/timetable', ({ request }) => {
        requestedUrl = request.url
        return HttpResponse.json([])
      }),
    )

    await apiGet('/api/timetable', { classId: 7471, from: '2026-11-02', to: '2026-11-06' })

    const url = new URL(requestedUrl)
    expect(url.pathname).toBe('/api/timetable')
    expect(url.searchParams.get('classId')).toBe('7471')
    expect(url.searchParams.get('from')).toBe('2026-11-02')
    expect(url.searchParams.get('to')).toBe('2026-11-06')
  })

  it('sends every request through the shared axios instance', async () => {
    const seen: string[] = []
    const interceptor = apiClient.interceptors.request.use((config) => {
      seen.push(config.url ?? '')
      return config
    })
    server.use(http.get('/api/me', () => HttpResponse.json({})))

    try {
      await apiGet('/api/me')
    } finally {
      apiClient.interceptors.request.eject(interceptor)
    }

    expect(seen).toEqual(['/api/me'])
  })

  it('turns a network failure into an ApiError with status 0', async () => {
    server.use(http.get('/api/me', () => HttpResponse.error()))

    const error = await apiGet('/api/me').catch((e: unknown) => e)

    expect(error).toBeInstanceOf(ApiError)
    expect((error as ApiError).status).toBe(0)
    expect((error as ApiError).title).toBe('Netzwerkfehler')
    expect((error as ApiError).detail).toBe('Der Server ist nicht erreichbar.')
  })

  it('rethrows errors that do not come from axios unchanged', async () => {
    const failure = new Error('interceptor failed')
    const interceptor = apiClient.interceptors.request.use(() => {
      throw failure
    })

    try {
      await expect(apiGet('/api/me')).rejects.toBe(failure)
    } finally {
      apiClient.interceptors.request.eject(interceptor)
    }
  })
})
