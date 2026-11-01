import type { RequestHandler } from 'msw'

/**
 * Default request handlers shared by all tests.
 * Modules register their own handlers here (e.g. `...gradesHandlers`) once they exist.
 */
export const handlers: RequestHandler[] = []
