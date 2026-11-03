import axios, { isAxiosError, type AxiosResponse } from 'axios'
import type { ModuleName, ProblemDetail } from './types'

/** Error thrown by the API client for every failed request. `status` is 0 when the server was not reachable. */
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

/** Query parameters of a GET request. */
export type QueryParams = Record<string, string | number>

/**
 * Shared axios instance for all backend calls. URLs are relative (`/api/...`) and proxied by Vite in
 * development and by nginx in the container. Cross-cutting concerns (headers, interceptors) live here.
 */
export const apiClient = axios.create({
  headers: { Accept: 'application/json' },
})

/**
 * Performs a GET request against the backend and returns the parsed JSON body.
 * Error responses are turned into an `ApiError` built from the RFC 9457 ProblemDetail body.
 */
export async function apiGet<T>(path: string, params?: QueryParams): Promise<T> {
  try {
    const response = await apiClient.get<T>(path, { params })
    return response.data
  } catch (error) {
    throw toApiError(error)
  }
}

function toApiError(error: unknown): unknown {
  if (!isAxiosError(error)) {
    return error
  }
  if (!error.response) {
    return new ApiError(0, 'Netzwerkfehler', 'Der Server ist nicht erreichbar.')
  }
  return fromResponse(error.response)
}

function fromResponse(response: AxiosResponse): ApiError {
  const problem = isProblemDetail(response.data) ? response.data : null
  const title = problem?.title ?? response.statusText ?? ''
  return new ApiError(
    response.status,
    title === '' ? `HTTP ${response.status}` : title,
    problem?.detail,
    problem?.module,
  )
}

function isProblemDetail(body: unknown): body is ProblemDetail {
  return typeof body === 'object' && body !== null
}
