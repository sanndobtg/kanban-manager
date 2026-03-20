import { describe, it, expect, vi } from 'vitest'
import api from '../src/services/api.js'

describe('API Axios', () => {
  it('a le baseURL /api', () => {
    expect(api.defaults.baseURL).toBe('/api')
  })

  it('ajoute le token Authorization quand il existe', () => {
    vi.spyOn(Storage.prototype, 'getItem').mockReturnValue('mon-jwt-token')

    const config = { headers: {} }
    const interceptor = api.interceptors.request.handlers[0].fulfilled
    const result = interceptor(config)

    expect(result.headers.Authorization).toBe('Bearer mon-jwt-token')
    vi.restoreAllMocks()
  })

  it('n\'ajoute pas Authorization quand pas de token', () => {
    vi.spyOn(Storage.prototype, 'getItem').mockReturnValue(null)

    const config = { headers: {} }
    const interceptor = api.interceptors.request.handlers[0].fulfilled
    const result = interceptor(config)

    expect(result.headers.Authorization).toBeUndefined()
    vi.restoreAllMocks()
  })
})