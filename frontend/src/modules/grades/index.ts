import { createElement } from 'react'
import type { ModuleDefinition } from '../../app/modules/types'
import { GradesPage } from './GradesPage'

export const gradesModule: ModuleDefinition = {
  name: 'grades',
  navItem: { label: 'Noten', path: '/noten', order: 50 },
  routes: [{ path: '/noten', element: createElement(GradesPage) }],
}
