import { HttpResponse, http, type RequestHandler } from 'msw'
import { dashboardFixture } from '../fixtures/dashboard'

/** Default handlers of the dashboard module. */
export const dashboardHandlers: RequestHandler[] = [http.get('/api/dashboard', () => HttpResponse.json(dashboardFixture))]
