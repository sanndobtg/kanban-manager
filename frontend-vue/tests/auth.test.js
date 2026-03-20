import { describe, it, expect, beforeEach, vi } from 'vitest'
import { setActivePinia, createPinia } from 'pinia'
import { useAuthStore } from '../src/stores/auth.js'

// ─── Mock authService ───────────────────────────────────────
vi.mock('../src/services/authService.js', () => ({
  authService: {
    login: vi.fn(),
  },
}))

// ─── Mock router ────────────────────────────────────────────
// On mock l'export par défaut du router
vi.mock('../src/router/index.js', () => ({
  default: {
    push: vi.fn(),
  },
}))

import { authService } from '../src/services/authService.js'
import router from '../src/router/index.js'

describe('Auth Store', () => {
  beforeEach(() => {
    vi.clearAllMocks()
    
    // Reset localStorage
    vi.spyOn(Storage.prototype, 'getItem').mockReturnValue(null)
    vi.spyOn(Storage.prototype, 'setItem').mockImplementation(() => {})
    vi.spyOn(Storage.prototype, 'removeItem').mockImplementation(() => {})
    
    setActivePinia(createPinia())
  })

  describe('État initial', () => {
    it('est non authentifié par défaut', () => {
      const store = useAuthStore()
      expect(store.token).toBeNull()
      expect(store.isAuthenticated).toBe(false)
    })

    it('restaure le token depuis localStorage', () => {
      vi.spyOn(Storage.prototype, 'getItem').mockImplementation((key) => {
        if (key === 'token') return 'saved-token'
        if (key === 'user') return JSON.stringify({ id: 1, role: 'UTILISATEUR' })
        return null
      })
      setActivePinia(createPinia())
      const store = useAuthStore()
      expect(store.token).toBe('saved-token')
    })

    it('restaure un utilisateur admin depuis localStorage', () => {
      vi.spyOn(Storage.prototype, 'getItem').mockImplementation((key) => {
        if (key === 'token') return 'admin-token'
        if (key === 'user') return JSON.stringify({ id: 1, role: 'ADMIN' })
        return null
      })
      setActivePinia(createPinia())
      const store = useAuthStore()
      expect(store.isAdmin).toBe(true)
    })
  })

  describe('login()', () => {
    it('stocke le token et l\'utilisateur après login réussi', async () => {
      const fakeResponse = {
        access_token: 'jwt-token-123',
        user: { id: 42, email: 'alice@test.com', role: 'UTILISATEUR' },
      }
      authService.login.mockResolvedValue(fakeResponse)
      const store = useAuthStore()
      await store.login('alice@test.com', 'motdepasse123')
      expect(store.token).toBe('jwt-token-123')
      expect(store.isAuthenticated).toBe(true)
    })

    it('persiste le token dans localStorage', async () => {
      const fakeResponse = { access_token: 'jwt-123', user: { id: 1 } }
      authService.login.mockResolvedValue(fakeResponse)
      const store = useAuthStore()
      await store.login('a@b.com', 'p')
      expect(Storage.prototype.setItem).toHaveBeenCalledWith('token', 'jwt-123')
    })

    it('propage l\'erreur si le login échoue', async () => {
      authService.login.mockRejectedValue(new Error('Erreur'))
      const store = useAuthStore()
      await expect(store.login('a@b.com', 'p')).rejects.toThrow('Erreur')
    })
  })

  describe('logout()', () => {
    it('réinitialise le token et l\'utilisateur', () => {
      const store = useAuthStore()
      store.token = 'token'
      store.logout()
      expect(store.token).toBeNull()
      expect(store.user).toBeNull()
    })

    it('supprime les données de localStorage', () => {
      const store = useAuthStore()
      store.logout()
      expect(Storage.prototype.removeItem).toHaveBeenCalledWith('token')
      expect(Storage.prototype.removeItem).toHaveBeenCalledWith('user')
    })

    it('redirige vers /login via le router', () => {
      const store = useAuthStore()
      store.logout()
      
      // On vérifie l'appel au mock du router
      expect(router.push).toHaveBeenCalledWith('/login')
    })
  })

  describe('Getters', () => {
    it('isAuthenticated est true quand le token existe', () => {
      const store = useAuthStore()
      store.token = 'abc'
      expect(store.isAuthenticated).toBe(true)
    })

    it('isAdmin est true pour un utilisateur ADMIN', () => {
      const store = useAuthStore()
      store.user = { role: 'ADMIN' }
      expect(store.isAdmin).toBe(true)
    })
  })
})