import { renderHook, waitFor } from '@testing-library/react'
import { HttpResponse, http } from 'msw'
import { describe, expect, it } from 'vitest'
import { ApiError } from '../../shared/api/client'
import { server } from '../../test/msw/server'
import type { AbsenceStatus } from './types'
import { useAbsences } from './useAbsences'

describe('useAbsences', () => {
  it('starts loading without data or error', () => {
    const { result } = renderHook(() => useAbsences())

    expect(result.current).toEqual({ data: null, loading: true, error: null })
  })

  it('provides the response once loaded', async () => {
    const { result } = renderHook(() => useAbsences())

    await waitFor(() => expect(result.current.loading).toBe(false))

    expect(result.current.error).toBeNull()
    expect(result.current.data?.summary.total).toBe(7)
    expect(result.current.data?.absences).toHaveLength(7)
  })

  it('loads only the absences of the given status', async () => {
    const { result } = renderHook(() => useAbsences('OPEN'))

    await waitFor(() => expect(result.current.loading).toBe(false))

    expect(result.current.data?.absences.map((absence) => absence.status)).toEqual(['OPEN', 'OPEN'])
  })

  it('refetches when the status changes and keeps the previous data meanwhile', async () => {
    const { result, rerender } = renderHook(({ status }: { status?: AbsenceStatus }) => useAbsences(status), {
      initialProps: {},
    })
    await waitFor(() => expect(result.current.loading).toBe(false))

    rerender({ status: 'UNEXCUSED' })

    expect(result.current.loading).toBe(true)
    expect(result.current.data?.absences).toHaveLength(7)
    await waitFor(() => expect(result.current.loading).toBe(false))
    expect(result.current.data?.absences.map((absence) => absence.subject)).toEqual([
      'Allgemeinbildung Gesellschaft',
      '450 Applikationen testen',
    ])
  })

  it('exposes the ApiError when the request fails', async () => {
    server.use(
      http.get('/api/absences', () =>
        HttpResponse.json({ title: 'Not Found', status: 404, detail: 'Benutzer unbekannt.' }, { status: 404 }),
      ),
    )
    const { result } = renderHook(() => useAbsences())

    await waitFor(() => expect(result.current.loading).toBe(false))

    expect(result.current.data).toBeNull()
    expect(result.current.error).toBeInstanceOf(ApiError)
    expect(result.current.error).toMatchObject({ status: 404, detail: 'Benutzer unbekannt.' })
  })

  it('clears a previous error when a later request succeeds', async () => {
    server.use(
      http.get('/api/absences', () => HttpResponse.json({ title: 'Bad Gateway', status: 502 }, { status: 502 }), {
        once: true,
      }),
    )
    const { result, rerender } = renderHook(({ status }: { status?: AbsenceStatus }) => useAbsences(status), {
      initialProps: {},
    })
    await waitFor(() => expect(result.current.error).not.toBeNull())

    rerender({ status: 'OPEN' })

    await waitFor(() => expect(result.current.loading).toBe(false))
    expect(result.current.error).toBeNull()
    expect(result.current.data?.absences).toHaveLength(2)
  })
})
