import { describe, expect, it } from 'vitest'
import { timetableModule } from './index'

describe('timetableModule', () => {
  it('is the backend module «timetable»', () => {
    expect(timetableModule.name).toBe('timetable')
    expect(timetableModule.enabledBy).toBeUndefined()
  })

  it('appears in the main navigation as «Stundenplan» at position 30', () => {
    expect(timetableModule.navItem).toEqual({ label: 'Stundenplan', path: '/stundenplan', order: 30 })
  })

  it('routes /stundenplan to the timetable page', () => {
    expect(timetableModule.routes).toHaveLength(1)
    expect(timetableModule.routes[0]?.path).toBe('/stundenplan')
    expect(timetableModule.routes[0]?.element).toBeDefined()
  })
})
