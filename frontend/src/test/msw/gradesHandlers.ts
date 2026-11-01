import { HttpResponse, http, type RequestHandler } from 'msw'
import { gradesOverviewFixture } from '../fixtures/grades'

/** Default handlers of the grades module (`GET /api/grades`). */
export const gradesHandlers: RequestHandler[] = [
  http.get('/api/grades', () => HttpResponse.json(gradesOverviewFixture)),
]
