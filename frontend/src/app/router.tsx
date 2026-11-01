import type { RouteObject, createBrowserRouter } from 'react-router-dom'
import type { ModuleRegistry } from './modules/registry'

export type AppRouter = ReturnType<typeof createBrowserRouter>

/** Route tree: layout route with every module route (guarded by module status) and a not-found fallback. */
export function buildAppRoutes(_registry: ModuleRegistry): RouteObject[] {
  throw new Error('not implemented')
}

/** Browser router for the application. */
export function createAppRouter(_registry?: ModuleRegistry): AppRouter {
  throw new Error('not implemented')
}
