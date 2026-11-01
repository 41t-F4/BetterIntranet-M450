import { HttpResponse, http, type RequestHandler } from 'msw'
import { currentUserFixture } from '../fixtures/currentUser'
import { allModulesEnabled } from '../fixtures/modules'
import { classesCoursesHandlers } from './classesCoursesHandlers'

/** Handlers for the endpoints the app shell needs at startup. */
export const shellHandlers: RequestHandler[] = [
  http.get('/api/me', () => HttpResponse.json(currentUserFixture)),
  http.get('/api/modules', () => HttpResponse.json(allModulesEnabled)),
]

/**
 * Default request handlers shared by all tests.
 * Feature modules add their own handlers here (e.g. `...gradesHandlers`) once they exist.
 */
export const handlers: RequestHandler[] = [...shellHandlers, ...classesCoursesHandlers]
