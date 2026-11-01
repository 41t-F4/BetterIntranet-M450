import { render, screen, within } from '@testing-library/react'
import { describe, expect, it } from 'vitest'
import { dashboardFixture } from '../../../test/fixtures/dashboard'
import { EventsCard } from './EventsCard'

function entry(title: string): HTMLElement {
  const item = screen.getByText(title).closest('li')
  if (!item) throw new Error(`No list item for ${title}`)
  return item
}

describe('EventsCard', () => {
  it('has the heading «Termine der Schule»', () => {
    render(<EventsCard events={dashboardFixture.events} />)

    expect(screen.getByRole('heading', { name: 'Termine der Schule' })).toBeInTheDocument()
  })

  it('lists every entry in the given order', () => {
    render(<EventsCard events={dashboardFixture.events} />)

    const items = within(screen.getByRole('list')).getAllByRole('listitem')
    expect(items).toHaveLength(2)
    expect(items[0]).toHaveTextContent('Elternabend Abteilung IT')
    expect(items[1]).toHaveTextContent('Weihnachtsferien')
  })

  it('shows a single date for a one-day event', () => {
    render(<EventsCard events={dashboardFixture.events} />)

    expect(within(entry('Elternabend Abteilung IT')).getByText('19.11.2026')).toBeInTheDocument()
  })

  it('shows the date range for a multi-day holiday', () => {
    render(<EventsCard events={dashboardFixture.events} />)

    expect(within(entry('Weihnachtsferien')).getByText('21.12.2026 – 02.01.2027')).toBeInTheDocument()
  })

  it('marks holidays with the badge «Ferien» and events with the badge «Anlass»', () => {
    render(<EventsCard events={dashboardFixture.events} />)

    expect(within(entry('Weihnachtsferien')).getByText('Ferien')).toBeInTheDocument()
    expect(within(entry('Weihnachtsferien')).queryByText('Anlass')).not.toBeInTheDocument()
    expect(within(entry('Elternabend Abteilung IT')).getByText('Anlass')).toBeInTheDocument()
  })

  it('shows a message when there are no upcoming entries', () => {
    render(<EventsCard events={[]} />)

    expect(screen.getByText('Keine Kalendereinträge gefunden.')).toBeInTheDocument()
    expect(screen.queryByRole('list')).not.toBeInTheDocument()
  })
})
