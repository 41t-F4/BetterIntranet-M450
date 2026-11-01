import { createElement } from 'react'
import type { ModuleDefinition } from '../../app/modules/types'
import { DashboardPage } from './DashboardPage'

export const dashboardModule: ModuleDefinition = {
  name: 'dashboard',
  navItem: { label: 'Home', path: '/', order: 10 },
  routes: [{ path: '/', element: createElement(DashboardPage) }],
}
