import { render, screen, within } from '@testing-library/react'
import { describe, expect, it } from 'vitest'
import { dashboardFixture } from '../../../test/fixtures/dashboard'
import { NewsCard } from './NewsCard'

describe('NewsCard', () => {
  it('has the heading «Newsticker»', () => {
    render(<NewsCard news={dashboardFixture.news} />)

    expect(screen.getByRole('heading', { name: 'Newsticker' })).toBeInTheDocument()
  })

  it('shows title, Swiss formatted date and text of every news entry in the given order', () => {
    render(<NewsCard news={dashboardFixture.news} />)

    const articles = screen.getAllByRole('article')
    expect(articles).toHaveLength(2)
    expect(within(articles[0]).getByRole('heading', { name: 'Tag der offenen Tür 2026' })).toBeInTheDocument()
    expect(within(articles[0]).getByText('28.10.2026')).toHaveAttribute('datetime', '2026-10-28')
    expect(
      within(articles[0]).getByText('Am Samstag öffnet die TBZ ihre Türen für Interessierte.'),
    ).toBeInTheDocument()
    expect(within(articles[1]).getByRole('heading', { name: 'Neue Mediothek-Öffnungszeiten' })).toBeInTheDocument()
  })

  it('shows the message of the old intranet when there is no news', () => {
    render(<NewsCard news={[]} />)

    expect(screen.getByText('Zur Zeit liegen keine Nachrichten vor.')).toBeInTheDocument()
    expect(screen.queryByRole('article')).not.toBeInTheDocument()
  })
})
