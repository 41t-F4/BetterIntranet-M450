import { describe, expect, it } from 'vitest'
import { allModulesEnabled, modulesWithDisabled } from '../../test/fixtures/modules'
import { createModuleRegistry, moduleRegistry } from './registry'
import type { ModuleDefinition, ModuleRouteHandle } from './types'

const grades: ModuleDefinition = {
  name: 'grades',
  navItem: { label: 'Noten', path: '/noten', order: 50 },
  routes: [{ path: '/noten', element: null }],
}

const dashboard: ModuleDefinition = {
  name: 'dashboard',
  navItem: { label: 'Home', path: '/', order: 10 },
  routes: [{ path: '/', element: null }],
}

const classesCourses: ModuleDefinition = {
  name: 'classes',
  enabledBy: ['classes', 'courses'],
  navItem: { label: 'Klassen & Kurse', path: '/klassen-kurse', order: 20 },
  routes: [{ path: '/klassen-kurse', element: null }],
}

const user: ModuleDefinition = {
  name: 'user',
  routes: [{ path: '/profil', element: null, handle: { breadcrumb: 'Profil' } }],
}

describe('createModuleRegistry', () => {
  describe('getNavItems', () => {
    it('returns the nav items sorted by order regardless of registration order', () => {
      const registry = createModuleRegistry([grades, classesCourses, dashboard])

      const labels = registry.getNavItems(allModulesEnabled).map((item) => item.label)

      expect(labels).toEqual(['Home', 'Klassen & Kurse', 'Noten'])
    })

    it('hides the nav item of a disabled module', () => {
      const registry = createModuleRegistry([dashboard, grades])

      const labels = registry.getNavItems(modulesWithDisabled('grades')).map((item) => item.label)

      expect(labels).toEqual(['Home'])
    })

    it('treats a module missing from the backend response as disabled', () => {
      const registry = createModuleRegistry([dashboard, grades])

      const labels = registry.getNavItems([{ name: 'dashboard', enabled: true }]).map((item) => item.label)

      expect(labels).toEqual(['Home'])
    })

    it('skips modules without a nav item', () => {
      const registry = createModuleRegistry([dashboard, user])

      const labels = registry.getNavItems(allModulesEnabled).map((item) => item.label)

      expect(labels).toEqual(['Home'])
    })

    describe('combined «Klassen & Kurse» item', () => {
      it('is shown when both classes and courses are enabled', () => {
        const registry = createModuleRegistry([classesCourses])

        expect(registry.getNavItems(allModulesEnabled)).toHaveLength(1)
      })

      it('is shown when only classes is enabled', () => {
        const registry = createModuleRegistry([classesCourses])

        expect(registry.getNavItems(modulesWithDisabled('courses'))).toHaveLength(1)
      })

      it('is shown when only courses is enabled', () => {
        const registry = createModuleRegistry([classesCourses])

        expect(registry.getNavItems(modulesWithDisabled('classes'))).toHaveLength(1)
      })

      it('is hidden when both classes and courses are disabled', () => {
        const registry = createModuleRegistry([classesCourses])

        expect(registry.getNavItems(modulesWithDisabled('classes', 'courses'))).toHaveLength(0)
      })
    })
  })

  describe('isEnabled', () => {
    it('is true when the backend reports the module as enabled', () => {
      const registry = createModuleRegistry([grades])

      expect(registry.isEnabled(grades, allModulesEnabled)).toBe(true)
    })

    it('is false when the backend reports the module as disabled', () => {
      const registry = createModuleRegistry([grades])

      expect(registry.isEnabled(grades, modulesWithDisabled('grades'))).toBe(false)
    })

    it('is true for a combined module when any of its backend modules is enabled', () => {
      const registry = createModuleRegistry([classesCourses])

      expect(registry.isEnabled(classesCourses, modulesWithDisabled('classes'))).toBe(true)
      expect(registry.isEnabled(classesCourses, modulesWithDisabled('classes', 'courses'))).toBe(false)
    })
  })

  describe('getRoutes', () => {
    it('collects the routes of all registered modules', () => {
      const registry = createModuleRegistry([dashboard, grades, user])

      const paths = registry.getRoutes().map((route) => route.path)

      expect(paths).toEqual(['/', '/noten', '/profil'])
    })

    it('annotates each route with its module and the nav label as breadcrumb', () => {
      const registry = createModuleRegistry([grades])

      const handle = registry.getRoutes()[0]?.handle as ModuleRouteHandle

      expect(handle.module).toBe(grades)
      expect(handle.breadcrumb).toBe('Noten')
    })

    it('keeps a breadcrumb label explicitly set on the route', () => {
      const registry = createModuleRegistry([user])

      const handle = registry.getRoutes()[0]?.handle as ModuleRouteHandle

      expect(handle.module).toBe(user)
      expect(handle.breadcrumb).toBe('Profil')
    })

    it('does not mutate the route objects of the definitions', () => {
      const registry = createModuleRegistry([grades])

      registry.getRoutes()

      expect(grades.routes[0]?.handle).toBeUndefined()
    })
  })

  describe('getDefinitions', () => {
    it('returns the definitions in registration order', () => {
      const registry = createModuleRegistry([grades, dashboard])

      expect(registry.getDefinitions()).toEqual([grades, dashboard])
    })
  })
})

describe('moduleRegistry (application registry)', () => {
  it('provides the main navigation of the intranet in the expected order', () => {
    const items = moduleRegistry.getNavItems(allModulesEnabled)

    expect(items.map((item) => [item.label, item.path])).toEqual([
      ['Home', '/'],
      ['Klassen & Kurse', '/klassen-kurse'],
      ['Stundenplan', '/stundenplan'],
      ['Absenzen', '/absenzen'],
      ['Noten', '/noten'],
    ])
  })

  it('registers a route for every navigation entry', () => {
    const paths = moduleRegistry.getRoutes().map((route) => route.path)

    expect(paths).toEqual(expect.arrayContaining(['/', '/klassen-kurse', '/stundenplan', '/absenzen', '/noten']))
  })
})
