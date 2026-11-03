import { Card } from '../../../shared/ui/Card'
import { formatDate } from '../lib/formatDate'
import type { NewsItem } from '../types'
import shared from './DashboardCard.module.css'
import styles from './NewsCard.module.css'

export interface NewsCardProps {
  news: readonly NewsItem[]
}

/** «Newsticker»: latest school news, newest first (order as delivered by the backend). */
export function NewsCard({ news }: NewsCardProps) {
  return (
    <Card title="Newsticker" className={shared.card}>
      {news.length === 0 ? (
        <p className={shared.empty}>Zur Zeit liegen keine Nachrichten vor.</p>
      ) : (
        <div className={styles.items}>
          {news.map((item) => (
            <article key={item.id} className={styles.item}>
              <time dateTime={item.publishedAt} className={shared.date}>
                {formatDate(item.publishedAt)}
              </time>
              <h3 className={styles.title}>{item.title}</h3>
              <p className={styles.text}>{item.text}</p>
            </article>
          ))}
        </div>
      )}
    </Card>
  )
}
