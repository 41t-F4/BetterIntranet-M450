import { HttpResponse, http } from 'msw'
import type { InternalAxiosRequestConfig } from 'axios'
import { describe, expect, it, vi } from 'vitest'
import { apiClient } from '../../../shared/api/client'
import { myCoursesFixture } from '../../../test/fixtures/courses'
import { server } from '../../../test/msw/server'
import { COURSES_API, fetchCourses } from './coursesApi'

describe('fetchCourses', () => {
  it('loads all courses from /api/courses when no semester is given', async () => {
    const requestedUrls = vi.fn<(url: string) => void>()
    server.use(
      http.get('/api/courses', ({ request }) => {
        requestedUrls(request.url)
        return HttpResponse.json(myCoursesFixture)
      }),
    )

    await expect(fetchCourses()).resolves.toEqual(myCoursesFixture)
    expect(requestedUrls).toHaveBeenCalledTimes(1)
    expect(new URL(requestedUrls.mock.calls[0]?.[0] ?? '').search).toBe('')
  })

  it('passes the semester as URL-encoded query parameter', async () => {
    const requestedUrls = vi.fn<(url: string) => void>()
    server.use(
      http.get('/api/courses', ({ request }) => {
        requestedUrls(request.url)
        return HttpResponse.json([])
      }),
    )

    await expect(fetchCourses('HS 2026')).resolves.toEqual([])
    expect(new URL(requestedUrls.mock.calls[0]?.[0] ?? '').search).toBe('?semester=HS+2026')
  })

  it('defines the base path once and hands the semester to axios as a query parameter', async () => {
    const requests: InternalAxiosRequestConfig[] = []
    const interceptor = apiClient.interceptors.request.use((config) => {
      requests.push(config)
      return config
    })
    server.use(http.get('/api/courses', () => HttpResponse.json([])))

    try {
      await fetchCourses('HS 2026')
      await fetchCourses()
    } finally {
      apiClient.interceptors.request.eject(interceptor)
    }

    expect(COURSES_API).toBe('/api/courses')
    expect(requests.map((request) => [request.url, request.params])).toEqual([
      [COURSES_API, { semester: 'HS 2026' }],
      [COURSES_API, undefined],
    ])
  })
})
