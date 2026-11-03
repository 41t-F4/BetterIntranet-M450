import { setupServer } from 'msw/node'
import { handlers } from './handlers'

/** MSW server used by every test: all `/api/*` calls are answered here, never by a real backend. */
export const server = setupServer(...handlers)
