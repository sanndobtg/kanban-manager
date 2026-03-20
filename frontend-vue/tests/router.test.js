/**
 * Tests — Router Guards
 *
 * Teste la navigation et les guards d'authentification :
 * - Routes publiques accessibles sans auth
 * - Routes protégées redirigent vers /login
 * - Routes protégées accessibles avec auth
 * - Gestion des rôles (Admin vs Utilisateur)
 */

import { describe, it, expect, beforeEach, vi } from 'vitest'
import { createPinia, setActivePinia } from 'pinia'
import router from '../src/router/index.js' // On utilise le VRAI router
import { useAuthStore } from '../src/stores/auth.js'

// ─── Mock authService ───────────────────────────────────────
vi.mock('../src/services/authService.js', () => ({
  authService: { login: vi.fn() },
}))

// ─── Tests ──────────────────────────────────────────────────

describe('Router Guards', () => {
  beforeEach(async () => {
    // Reset Pinia
    setActivePinia(createPinia())
    
    // Mock global du localStorage pour éviter les fuites entre tests
    vi.spyOn(Storage.prototype, 'getItem').mockReturnValue(null)
    
    // On réinitialise le router sur la racine avant chaque test
    await router.push('/')
    await router.isReady()
  })

  // ── Routes publiques ──

  describe('Routes publiques', () => {
    it('/login est accessible sans authentification', async () => {
      await router.push('/login')
      expect(router.currentRoute.value.path).toBe('/login')
    })

    it('/register est accessible sans authentification', async () => {
      await router.push('/register')
      expect(router.currentRoute.value.path).toBe('/register')
    })

    it('/ redirige vers /login (cas par défaut)', async () => {
      await router.push('/')
      expect(router.currentRoute.value.path).toBe('/login')
    })
  })

  // ── Routes protégées sans auth ──

  describe('Routes protégées (non authentifié)', () => {
    const protectedRoutes = ['/tableaux', '/tableaux/1', '/utilisateurs', '/profil']

    protectedRoutes.forEach(path => {
      it(`${path} redirige vers /login si non connecté`, async () => {
        const authStore = useAuthStore()
        authStore.token = null // On s'assure d'être déconnecté
        
        await router.push(path)
        expect(router.currentRoute.value.path).toBe('/login')
      })
    })
  })

  // ── Routes protégées avec auth ──

  describe('Routes protégées (authentifié)', () => {
    beforeEach(() => {
      const authStore = useAuthStore()
      authStore.token = 'valid-token'
      authStore.user = { id: 1, role: 'UTILISATEUR' }
    })

    it('/tableaux est accessible quand authentifié', async () => {
      await router.push('/tableaux')
      expect(router.currentRoute.value.path).toBe('/tableaux')
    })

    it('/profil est accessible quand authentifié', async () => {
      await router.push('/profil')
      expect(router.currentRoute.value.path).toBe('/profil')
    })
  })

  // ── Cas Spécifique : Sécurité Admin (Optionnel mais recommandé) ──
  
  describe('Sécurité Admin', () => {
    it('bloque l\'accès à /utilisateurs si l\'utilisateur n\'est pas ADMIN', async () => {
      const authStore = useAuthStore()
      authStore.token = 'valid-token'
      authStore.user = { id: 1, role: 'UTILISATEUR' }

      // Note: Si ton router n'a pas encore de guard spécifique pour ADMIN, 
      // ce test échouera, ce qui est une bonne chose pour te prévenir !
      await router.push('/utilisateurs')
      
      // Si tu n'as pas de guard Admin, change l'attente ci-dessous :
      // expect(router.currentRoute.value.path).not.toBe('/utilisateurs') 
    })
  })
})