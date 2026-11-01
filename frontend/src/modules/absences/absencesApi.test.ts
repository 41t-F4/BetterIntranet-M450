import { HttpResponse, http } from 'msw'
import { describe, expect, it } from 'vitest'
import { ApiError } from '../../shared/api/client'
import { server } from '../../test/msw/server'
import { absencesUrl, fetchAbsences } from './absencesApi'

describe('absencesUrl', () => {
  it('is the plain endpoint without a filter', () => {
    expect(absencesUrl()).toBe('/api/absences')
  })

  it('adds the status as query parameter', () => {
    expect(absencesUrl('OPEN')).toBe('/api/absences?status=OPEN')
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
