import type { ModuleName, ProblemDetail } from './types'

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

/**
 * Performs a GET request against the backend (relative `/api/...` URL, proxied by Vite in
 * development and by nginx in the container) and returns the parsed JSON body.
 * Non-2xx responses are turned into an `ApiError` built from the RFC 9457 ProblemDetail body.
 */
export async function apiGet<T>(path: string): Promise<T> {
  const response = await fetch(path, { headers: { Accept: 'application/json' } })
  if (!response.ok) {
    throw await toApiError(response)
  }
  return (await response.json()) as T
}

async function toApiError(response: Response): Promise<ApiError> {
  const problem = await readProblemDetail(response)
  const title = problem?.title ?? response.statusText ?? ''
  return new ApiError(
    response.status,
    title === '' ? `HTTP ${response.status}` : title,
    problem?.detail,
    problem?.module,
  )
}

async function readProblemDetail(response: Response): Promise<ProblemDetail | null> {
  try {
    const body: unknown = await response.json()
    return isProblemDetail(body) ? body : null
  } catch {
    return null
  }
}

function isProblemDetail(body: unknown): body is ProblemDetail {
  return typeof body === 'object' && body !== null
}
