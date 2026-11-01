import { describe, expect, it } from 'vitest'
import { handlers, moduleHandlers, shellHandlers } from './handlers'

describe('MSW handler registry', () => {
  it('always contains the shell handlers first', () => {
    expect(handlers.slice(0, shellHandlers.length)).toEqual(shellHandlers)
  })

  it('appends every discovered module handler after the shell handlers', () => {
    expect(handlers).toHaveLength(shellHandlers.length + moduleHandlers.length)
  })
})
