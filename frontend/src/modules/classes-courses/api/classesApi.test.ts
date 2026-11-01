import { HttpResponse, http } from 'msw'
import { describe, expect, it } from 'vitest'
import { myClassesFixture } from '../../../test/fixtures/classes'
import { server } from '../../../test/msw/server'
import { ApiError } from '../../../shared/api/client'
import { fetchMyClasses } from './classesApi'

describe('fetchMyClasses', () => {
  it('loads the classes of the logged-in user from /api/classes', async () => {
    await expect(fetchMyClasses()).resolves.toEqual(myClassesFixture)
  })

  it('throws an ApiError with the module name when the module is disabled', async () => {
    server.use(
      http.get('/api/classes', () =>
        HttpResponse.json(
          { title: 'Service Unavailable', status: 503, detail: 'Modul deaktiviert.', module: 'classes' },
          { status: 503 },
        ),
      ),
    )

    const error = await fetchMyClasses().catch((reason: unknown) => reason)

    expect(error).toBeInstanceOf(ApiError)
    expect(error).toMatchObject({ status: 503, module: 'classes' })
  })
})
