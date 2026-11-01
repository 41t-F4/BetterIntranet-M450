import { describe, expect, it } from 'vitest'
import { dashboardModule } from './index'

describe('dashboardModule', () => {
  it('is registered under the backend module name «dashboard»', () => {
    expect(dashboardModule.name).toBe('dashboard')
  })

  it('is the first navigation entry «Home» on the start page', () => {
    expect(dashboardModule.navItem).toEqual({ label: 'Home', path: '/', order: 10 })
  })

  it('serves the dashboard page on /', () => {
    expect(dashboardModule.routes.map((route) => route.path)).toEqual(['/'])
    expect(dashboardModule.routes[0].element).toBeDefined()
  })
})
