export interface DisabledModulePageProps {
  /** Label of the disabled module, shown as page title. */
  title: string
}

/** Shown in place of a module page while the backend reports the module as disabled. */
export function DisabledModulePage(_props: DisabledModulePageProps) {
  return null
}
