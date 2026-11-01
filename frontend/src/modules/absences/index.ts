import { createElement } from 'react'
import type { ModuleDefinition } from '../../app/modules/types'
import { AbsencesPage } from './AbsencesPage'

export const absencesModule: ModuleDefinition = {
  name: 'absences',
  navItem: { label: 'Absenzen', path: '/absenzen', order: 40 },
  routes: [{ path: '/absenzen', element: createElement(AbsencesPage) }],
}
