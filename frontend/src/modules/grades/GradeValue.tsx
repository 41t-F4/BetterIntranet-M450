import { formatGrade, isInsufficient } from './lib/format'
import styles from './GradeValue.module.css'

export interface GradeValueProps {
  value: number | null
  className?: string
}

/**
 * A formatted grade or average. Values below 4.0 are rendered in the danger colour and labelled
 * «ungenügend» for assistive technology.
 */
export function GradeValue({ value, className }: GradeValueProps) {
  const insufficient = isInsufficient(value)
  const classes = [className, insufficient ? styles.insufficient : undefined].filter(Boolean).join(' ')
  return (
    <span
      className={classes === '' ? undefined : classes}
      aria-label={insufficient ? 'ungenügend' : undefined}
      title={insufficient ? 'ungenügend' : undefined}
    >
      {formatGrade(value)}
    </span>
  )
}
