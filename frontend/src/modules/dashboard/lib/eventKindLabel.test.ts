import { describe, expect, it } from 'vitest'
import { eventKindLabel } from './eventKindLabel'

describe('eventKindLabel', () => {
  it('labels holidays as «Ferien»', () => {
    expect(eventKindLabel('HOLIDAY')).toBe('Ferien')
  })

  it('labels school events as «Anlass»', () => {
    expect(eventKindLabel('EVENT')).toBe('Anlass')
  })
})
