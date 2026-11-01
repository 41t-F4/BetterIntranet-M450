import { HttpResponse, http, type RequestHandler } from 'msw'
import { currentUserFixture } from '../fixtures/currentUser'
import { allModulesEnabled } from '../fixtures/modules'

/** Handlers for the endpoints the app shell needs at startup. */
export const shellHandlers: RequestHandler[] = [
  http.get('/api/me', () => HttpResponse.json(currentUserFixture)),
  http.get('/api/modules', () => HttpResponse.json(allModulesEnabled)),
]

/**
 * Every feature module ships its default handlers in `./<module>Handlers.ts` and exports them as a
 * `RequestHandler[]`. They are discovered here so feature branches never have to edit this file,
 * which keeps parallel module branches free of merge conflicts.
 */
const moduleHandlerFiles = import.meta.glob<Record<string, RequestHandler[]>>('./*Handlers.ts', {
  eager: true,
})

export const moduleHandlers: RequestHandler[] = Object.values(moduleHandlerFiles).flatMap((exports) =>
  Object.values(exports).flat(),
)

/** Default request handlers shared by all tests. */
export const handlers: RequestHandler[] = [...shellHandlers, ...moduleHandlers]
