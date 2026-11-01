import type { HTMLAttributes, ReactNode } from 'react'
import styles from './Card.module.css'

export interface CardProps extends HTMLAttributes<HTMLElement> {
  /** Card title, rendered as heading of the given level. */
  title?: string
  /** Heading level of the title; the first card on a page uses level 1. */
  headingLevel?: 1 | 2 | 3
  children?: ReactNode
}

/** White content card in the «Marks» style: small radius, subtle shadow, generous padding. */
export function Card({ title, headingLevel = 2, children, className, ...rest }: CardProps) {
  const Heading = `h${headingLevel}` as const
  return (
    <section className={[styles.card, className].filter(Boolean).join(' ')} {...rest}>
      {title && <Heading className={styles.title}>{title}</Heading>}
      {children}
    </section>
  )
}
