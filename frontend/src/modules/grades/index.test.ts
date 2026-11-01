import { describe, expect, it } from 'vitest'
import { gradesModule } from './index'

describe('gradesModule', () => {
  it('is registered under the backend module name «grades»', () => {
    expect(gradesModule.name).toBe('grades')
  })

  it('is reachable under /noten as «Noten» at the end of the navigation', () => {
    expect(gradesModule.navItem).toEqual({ label: 'Noten', path: '/noten', order: 50 })
  })

  it('provides exactly one route at /noten with a page element', () => {
    expect(gradesModule.routes).toHaveLength(1)
    expect(gradesModule.routes[0]?.path).toBe('/noten')
    expect(gradesModule.routes[0]?.element).toBeTruthy()
  })
})
