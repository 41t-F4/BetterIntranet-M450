export interface DashboardPageProps {
  /** Clock used for the greeting; injectable for deterministic tests. */
  now?: () => Date
}

/** Home page: greeting plus newsticker, school events, prevention box and link lists. */
export function DashboardPage(_props: DashboardPageProps) {
  return null
}
