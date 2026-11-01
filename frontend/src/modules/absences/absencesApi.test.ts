import { HttpResponse, http } from 'msw'
import type { InternalAxiosRequestConfig } from 'axios'
import { describe, expect, it } from 'vitest'
import { ApiError, apiClient } from '../../shared/api/client'
import { server } from '../../test/msw/server'
import { ABSENCES_API, fetchAbsences } from './absencesApi'

describe('absences request', () => {
  it('defines the base path once and hands the status filter to axios as a query parameter', async () => {
    const requests: InternalAxiosRequestConfig[] = []
    const interceptor = apiClient.interceptors.request.use((config) => {
      requests.push(config)
      return config
    })

    try {
      await fetchAbsences('OPEN')
      await fetchAbsences()
    } finally {
      apiClient.interceptors.request.eject(interceptor)
    }

    expect(ABSENCES_API).toBe('/api/absences')
    expect(requests.map((request) => [request.url, request.params])).toEqual([
      [ABSENCES_API, { status: 'OPEN' }],
      [ABSENCES_API, undefined],
    ])
  })
})

describe('fetchAbsences', () => {
  it('returns summary and all absences without a filter', async () => {
    const response = await fetchAbsences()

    expect(response.summary.total).toBe(7)
    expect(response.absences).toHaveLength(7)
  })

  it('returns only the absences of the requested status while the summary covers all', async () => {
    const response = await fetchAbsences('EXCUSED')

    expect(response.absences.map((absence) => absence.status)).toEqual(['EXCUSED', 'EXCUSED', 'EXCUSED'])
    expect(response.summary.total).toBe(7)
  })

  it('throws an ApiError with the problem detail when the school system is unreachable', async () => {
    server.use(
      http.get('/api/absences', () =>
        HttpResponse.json(
          { title: 'Bad Gateway', status: 502, detail: 'Schulsystem nicht erreichbar.', upstream: 'school' },
          { status: 502 },
        ),
      ),
    )

    const error = await fetchAbsences().catch((reason: unknown) => reason)

    expect(error).toBeInstanceOf(ApiError)
    expect(error).toMatchObject({ status: 502, detail: 'Schulsystem nicht erreichbar.' })
  })
})
