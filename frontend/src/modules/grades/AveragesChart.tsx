import type { AveragesChartDatum } from './lib/averages'

export interface AveragesChartProps {
  data: AveragesChartDatum[]
  /** Fixed width in px; without it the chart fills its container. */
  width?: number
  /** Fixed height in px; without it the chart fills its container. */
  height?: number
}

/** Grouped bar chart of the averages per school part («Durchschnitt» vs. «Zeugnis»). */
export function AveragesChart(_props: AveragesChartProps) {
  return null
}
