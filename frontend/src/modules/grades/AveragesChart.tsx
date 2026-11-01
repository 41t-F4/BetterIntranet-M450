import { Bar, BarChart, CartesianGrid, Legend, ResponsiveContainer, Tooltip, XAxis, YAxis } from 'recharts'
import type { AveragesChartDatum } from './lib/averages'
import { formatGrade } from './lib/format'

export interface AveragesChartProps {
  data: AveragesChartDatum[]
  /** Fixed width in px; without it the chart fills its container. */
  width?: number
  /** Fixed height in px; without it the chart fills its container. */
  height?: number
}

export const CHART_LABEL = 'Balkendiagramm der Notendurchschnitte'
const DEFAULT_HEIGHT = 260
const GRADE_TICKS = [1, 2, 3, 4, 5, 6]

/**
 * Grouped bar chart with one group per subject («Durchschnitt» vs. «Zeugnis»), labelled with the
 * short subject label.
 * The wrapper is exposed as an image; the numbers themselves are available in the summary table
 * next to the chart, so the SVG internals stay hidden from assistive technology.
 */
export function AveragesChart({ data, width, height }: AveragesChartProps) {
  const chart = (
    <BarChart data={data} width={width} height={height} margin={{ top: 8, right: 8, bottom: 0, left: -16 }} barGap={2}>
      <CartesianGrid vertical={false} stroke="var(--color-border)" />
      <XAxis dataKey="label" tickLine={false} axisLine={{ stroke: 'var(--color-border)' }} tick={tickStyle} />
      <YAxis domain={[1, 6]} ticks={GRADE_TICKS} tickLine={false} axisLine={false} tick={tickStyle} />
      <Tooltip cursor={{ fill: 'var(--color-surface-muted)' }} formatter={formatTooltipValue} />
      <Legend iconType="square" iconSize={10} wrapperStyle={legendStyle} />
      <Bar dataKey="average" name="Durchschnitt" fill="var(--color-series-average)" radius={BAR_RADIUS} maxBarSize={40} isAnimationActive={false} />
      <Bar dataKey="reportGrade" name="Zeugnis" fill="var(--color-series-report)" radius={BAR_RADIUS} maxBarSize={40} isAnimationActive={false} />
    </BarChart>
  )

  return (
    <div role="img" aria-label={CHART_LABEL}>
      {width !== undefined && height !== undefined ? (
        chart
      ) : (
        <ResponsiveContainer width="100%" height={height ?? DEFAULT_HEIGHT}>
          {chart}
        </ResponsiveContainer>
      )}
    </div>
  )
}

const BAR_RADIUS: [number, number, number, number] = [3, 3, 0, 0]
const tickStyle = { fill: 'var(--color-text-subtle)', fontSize: 12 }
const legendStyle = { fontSize: 13, color: 'var(--color-text-muted)', paddingTop: 8 }

function formatTooltipValue(value: unknown): string {
  return typeof value === 'number' ? formatGrade(value) : formatGrade(null)
}
