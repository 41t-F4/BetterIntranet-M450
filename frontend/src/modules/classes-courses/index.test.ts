import { describe, expect, it } from 'vitest'
import { classesCoursesModule } from './index'

describe('classesCoursesModule', () => {
  it('is registered under the backend module name classes', () => {
    expect(classesCoursesModule.name).toBe('classes')
  })

  it('is visible as long as either classes or courses is enabled', () => {
    expect(classesCoursesModule.enabledBy).toEqual(expect.arrayContaining(['classes', 'courses']))
  })

  it('is reachable under /klassen-kurse with a navigation entry', () => {
    expect(classesCoursesModule.navItem).toEqual({ label: 'Klassen & Kurse', path: '/klassen-kurse', order: 20 })
    expect(classesCoursesModule.routes.map((route) => route.path)).toEqual(['/klassen-kurse'])
  })
})
