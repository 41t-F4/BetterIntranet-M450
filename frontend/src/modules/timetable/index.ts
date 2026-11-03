import { createElement } from 'react'
import type { ModuleDefinition } from '../../app/modules/types'
import { TimetablePage } from './TimetablePage'

export const timetableModule: ModuleDefinition = {
  name: 'timetable',
  navItem: { label: 'Stundenplan', path: '/stundenplan', order: 30 },
  routes: [{ path: '/stundenplan', element: createElement(TimetablePage) }],
}
