import { Card } from '../../shared/ui/Card'

export interface DisabledModulePageProps {
  /** Label of the disabled module, shown as page title. */
  title: string
}

/** Shown in place of a module page while the backend reports the module as disabled. */
export function DisabledModulePage({ title }: DisabledModulePageProps) {
  return (
    <Card title={title} headingLevel={1}>
      <p>Dieses Modul ist zurzeit deaktiviert.</p>
    </Card>
  )
}
