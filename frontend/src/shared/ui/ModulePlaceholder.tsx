import { Card } from './Card'

/** Placeholder page of a module that is not implemented yet. */
export function ModulePlaceholder({ title }: { title: string }) {
  return (
    <Card title={title} headingLevel={1}>
      <p>Dieses Modul ist in Arbeit.</p>
    </Card>
  )
}
