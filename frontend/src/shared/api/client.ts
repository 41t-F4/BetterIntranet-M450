import type { ModuleName } from './types'

/** Error thrown by the API client for every non-2xx response. */
export class ApiError extends Error {
  readonly status: number
  readonly title: string
  readonly detail: string | undefined
  readonly module: ModuleName | undefined

  constructor(status: number, title: string, detail?: string, module?: ModuleName) {
    super(`${status} ${title}`)
    this.name = 'ApiError'
    this.status = status
    this.title = title
    this.detail = detail
    this.module = module
  }
}

export function apiGet<T>(_path: string): Promise<T> {
  throw new Error('not implemented')
}
