import { render, screen } from '@testing-library/react'
import { describe, expect, it } from 'vitest'
import { AveragesChart } from './AveragesChart'
import type { AveragesChartDatum } from './lib/averages'

const data: AveragesChartDatum[] = [
  { schoolPart: 'BMS', label: 'BMS', average: 4.88, reportGrade: 5.0 },
  { schoolPart: 'TBZ', label: 'TBZ', average: 5.0, reportGrade: 5.0 },
  { schoolPart: 'UEK', label: 'ÜK', average: null, reportGrade: null },
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

  it('renders one axis tick per school part', () => {
    render(<AveragesChart data={data} width={600} height={300} />)

    expect(screen.getByText('BMS')).toBeInTheDocument()
    expect(screen.getByText('TBZ')).toBeInTheDocument()
    expect(screen.getByText('ÜK')).toBeInTheDocument()
  })
})
