import { createBrowserRouter, type RouteObject } from 'react-router-dom'
import { AppLayout } from './layout/AppLayout'
import { ErrorBoundary } from './layout/ErrorBoundary'
import { ModuleGuard } from './layout/ModuleGuard'
import { NotFoundPage } from './layout/NotFoundPage'
import { moduleRegistry, type ModuleRegistry } from './modules/registry'
import type { ModuleRouteHandle } from './modules/types'

export type AppRouter = ReturnType<typeof createBrowserRouter>

const NOT_FOUND_HANDLE: ModuleRouteHandle = { breadcrumb: 'Seite nicht gefunden' }

/** Route tree: layout route with every module route (guarded by module status) and a not-found fallback. */
export function buildAppRoutes(registry: ModuleRegistry): RouteObject[] {
  return [
    {
      path: '/',
      element: <AppLayout registry={registry} />,
      errorElement: <ErrorBoundary />,
      children: [
        {
          element: <ModuleGuard registry={registry} />,
          children: registry.getRoutes(),
        },
        { path: '*', element: <NotFoundPage />, handle: NOT_FOUND_HANDLE },
      ],
    },
  ]
}

/** Browser router for the application. */
export function createAppRouter(registry: ModuleRegistry = moduleRegistry): AppRouter {
  return createBrowserRouter(buildAppRoutes(registry))
}
