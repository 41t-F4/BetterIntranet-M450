import { render, type RenderResult } from '@testing-library/react'
import type { ReactElement } from 'react'
import { MemoryRouter } from 'react-router-dom'

/** Renders a component that needs router context (links, NavLink) at the given path. */
export function renderWithRouter(ui: ReactElement, { path = '/' }: { path?: string } = {}): RenderResult {
  return render(<MemoryRouter initialEntries={[path]}>{ui}</MemoryRouter>)
}
