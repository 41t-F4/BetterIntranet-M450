import { HttpResponse, http, type RequestHandler } from 'msw'
import { isAbsenceStatus } from '../../modules/absences/lib/status'
import { absencesResponseForStatus } from '../fixtures/absences'

/** Default handlers of the absences module: `GET /api/absences` with optional `?status=` filter. */
export const absencesHandlers: RequestHandler[] = [
  http.get('/api/absences', ({ request }) => {
    const status = new URL(request.url).searchParams.get('status')
    if (status !== null && !isAbsenceStatus(status)) {
      return HttpResponse.json(
        { type: 'about:blank', title: 'Bad Request', status: 400, detail: `Unbekannter Status: ${status}` },
        { status: 400 },
      )
    }
    return HttpResponse.json(absencesResponseForStatus(status))
  }),
]
