/**
 * Tests — Auth Service
 *
 * Teste le service d'authentification :
 *   - Appel fetch avec les bons paramètres
 *   - Gestion des erreurs
 *   - Parsing de la réponse
 */

import { describe, it, expect, beforeEach, vi } from 'vitest'

// ─── Mock fetch ─────────────────────────────────────────────

const mockFetch = vi.fn()
globalThis.fetch = mockFetch

// ─── Tests ──────────────────────────────────────────────────

describe('Auth Service', () => {
  beforeEach(() => {
    vi.clearAllMocks()
    vi.resetModules()
  })

  it('appelle POST /login avec email et motDePasse', async () => {
    const fakeResponse = {
      access_token: 'jwt-123',
      user: { id: 1, email: 'test@test.com' },
    }

    mockFetch.mockResolvedValue({
      ok: true,
      json: () => Promise.resolve(fakeResponse),
    })

    const { authService } = await import('../src/services/authService.js')
    await authService.login('test@test.com', 'pass123')

    expect(mockFetch).toHaveBeenCalledWith('http://localhost:3000/login', {
      method: 'POST',
      headers: { 'Content-Type': 'application/json' },
      body: JSON.stringify({ email: 'test@test.com', motDePasse: 'pass123' }),
    })
  })

  it('retourne les données de login en cas de succès', async () => {
    const fakeResponse = {
      access_token: 'jwt-456',
      token_type: 'Bearer',
      user: { id: 42, nom: 'Dupont', email: 'alice@test.com', role: 'UTILISATEUR' },
    }

    mockFetch.mockResolvedValue({
      ok: true,
      json: () => Promise.resolve(fakeResponse),
    })

    const { authService } = await import('../src/services/authService.js')
    const result = await authService.login('alice@test.com', 'motdepasse123')

    expect(result.access_token).toBe('jwt-456')
    expect(result.user.email).toBe('alice@test.com')
    expect(result.user.role).toBe('UTILISATEUR')
  })

  it('lance une erreur si la réponse est non-ok (401)', async () => {
    mockFetch.mockResolvedValue({
      ok: false,
      status: 401,
    })

    const { authService } = await import('../src/services/authService.js')

    await expect(authService.login('bad@test.com', 'wrong'))
      .rejects.toThrow('Identifiants incorrects')
  })

  it('lance une erreur si le serveur est injoignable', async () => {
    mockFetch.mockRejectedValue(new TypeError('Failed to fetch'))

    const { authService } = await import('../src/services/authService.js')

    await expect(authService.login('test@test.com', 'pass'))
      .rejects.toThrow('Failed to fetch')
  })
})