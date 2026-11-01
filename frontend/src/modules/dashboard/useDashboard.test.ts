import { renderHook, waitFor } from '@testing-library/react'
import { HttpResponse, http } from 'msw'
import { describe, expect, it } from 'vitest'
import { ApiError } from '../../shared/api/client'
import { dashboardFixture } from '../../test/fixtures/dashboard'
import { server } from '../../test/msw/server'
import { useDashboard } from './useDashboard'

describe('useDashboard', () => {
  it('starts in the loading state', () => {
    const { result } = renderHook(() => useDashboard())

    expect(result.current).toEqual({ status: 'loading' })
  })

  it('provides the dashboard from GET /api/dashboard', async () => {
    const { result } = renderHook(() => useDashboard())

    await waitFor(() => expect(result.current.status).toBe('ready'))
    expect(result.current).toEqual({ status: 'ready', data: dashboardFixture })
  })

  it('exposes the ApiError with the ProblemDetail detail when the request fails', async () => {
    server.use(
      http.get('/api/dashboard', () =>
        HttpResponse.json(
          { title: 'Bad Gateway', status: 502, detail: 'Ferienkalender nicht erreichbar.' },
          { status: 502 },
        ),
      ),
    )

    const { result } = renderHook(() => useDashboard())

    await waitFor(() => expect(result.current.status).toBe('error'))
    const state = result.current
    if (state.status !== 'error') throw new Error('expected error state')
    expect(state.error).toBeInstanceOf(ApiError)
    expect((state.error as ApiError).status).toBe(502)
    expect((state.error as ApiError).detail).toBe('Ferienkalender nicht erreichbar.')
  })
})
