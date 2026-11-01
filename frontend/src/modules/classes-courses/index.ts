import { createElement } from 'react'
import type { ModuleDefinition } from '../../app/modules/types'
import { ClassesCoursesPage } from './ClassesCoursesPage'

/** «Klassen & Kurse» combines the backend modules `classes` and `courses` in one page. */
export const classesCoursesModule: ModuleDefinition = {
  name: 'classes',
  enabledBy: ['classes', 'courses'],
  navItem: { label: 'Klassen & Kurse', path: '/klassen-kurse', order: 20 },
  routes: [{ path: '/klassen-kurse', element: createElement(ClassesCoursesPage) }],
}
