import { screen } from '@testing-library/react'
import type { ReactElement } from 'react'
import { describe, expect, it } from 'vitest'
import { renderWithRouter } from '../../test/render'
import { absencesModule } from './index'

describe('absencesModule', () => {
  it('is registered under the backend module name «absences»', () => {
    expect(absencesModule.name).toBe('absences')
  })

  it('has the nav item «Absenzen» at /absenzen with order 40', () => {
    expect(absencesModule.navItem).toEqual({ label: 'Absenzen', path: '/absenzen', order: 40 })
  })

  it('routes /absenzen to the absences page', async () => {
    const route = absencesModule.routes[0]
    expect(route?.path).toBe('/absenzen')

    renderWithRouter(route?.element as ReactElement, { path: '/absenzen' })

    expect(await screen.findByRole('heading', { level: 2, name: 'Einträge' })).toBeInTheDocument()
  })
})
