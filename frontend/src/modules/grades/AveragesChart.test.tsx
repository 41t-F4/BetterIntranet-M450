import { render, screen } from '@testing-library/react'
import { describe, expect, it } from 'vitest'
import { AveragesChart } from './AveragesChart'
import type { AveragesChartDatum } from './lib/averages'

const data: AveragesChartDatum[] = [
  { subject: '450 Applikationen testen', label: 'M450', average: 5.17, reportGrade: 5.0 },
  { subject: 'Allgemeinbildung Gesellschaft', label: 'ABU', average: 4.0, reportGrade: 4.0 },
  { subject: 'Englisch', label: 'Englisch', average: null, reportGrade: null },
]

describe('AveragesChart', () => {
  it('is exposed as an image with a descriptive label', () => {
    render(<AveragesChart data={data} width={600} height={300} />)

    expect(screen.getByRole('img', { name: 'Balkendiagramm der Notendurchschnitte' })).toBeInTheDocument()
  })

  it('renders both series with their legend entries when given a fixed size', () => {
    const { container } = render(<AveragesChart data={data} width={600} height={300} />)

    expect(container.querySelector('svg')).not.toBeNull()
    expect(screen.getByText('Durchschnitt')).toBeInTheDocument()
    expect(screen.getByText('Zeugnis')).toBeInTheDocument()
  })

  it('renders one axis tick per subject with its short label', () => {
    render(<AveragesChart data={data} width={600} height={300} />)

    expect(screen.getByText('M450')).toBeInTheDocument()
    expect(screen.getByText('ABU')).toBeInTheDocument()
    expect(screen.getByText('Englisch')).toBeInTheDocument()
  })
})
