import type { InternalAxiosRequestConfig } from 'axios'
import { afterEach, beforeEach, describe, expect, it } from 'vitest'
import { apiClient } from '../../shared/api/client'
import { FIXTURE_WEEK_END, FIXTURE_WEEK_START } from '../../test/fixtures/timetable'
import { TIMETABLE_API, fetchHolidays, fetchLessons, fetchTimetableClasses } from './timetableApi'

describe('timetableApi', () => {
  let requests: InternalAxiosRequestConfig[]
  let interceptor: number

  beforeEach(() => {
    requests = []
    interceptor = apiClient.interceptors.request.use((config) => {
      requests.push(config)
      return config
    })
  })

  afterEach(() => apiClient.interceptors.request.eject(interceptor))

  it('defines the base path of the timetable API once', () => {
    expect(TIMETABLE_API).toBe('/api/timetable')
  })

  it('requests the classes below the base path', async () => {
    await fetchTimetableClasses()

    expect(requests.map((request) => request.url)).toEqual([`${TIMETABLE_API}/classes`])
  })

  it('requests the holidays below the base path', async () => {
    await fetchHolidays()

    expect(requests.map((request) => request.url)).toEqual([`${TIMETABLE_API}/holidays`])
  })

  it('passes class and date range as query parameters instead of building the URL by hand', async () => {
    await fetchLessons(7471, FIXTURE_WEEK_START, FIXTURE_WEEK_END)

    expect(requests).toHaveLength(1)
    expect(requests[0].url).toBe(TIMETABLE_API)
    expect(requests[0].params).toEqual({ classId: 7471, from: FIXTURE_WEEK_START, to: FIXTURE_WEEK_END })
  })
})
