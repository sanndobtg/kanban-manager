<template>
  <div id="app">
    <nav v-if="authStore.isAuthenticated" class="navbar">
      <div class="navbar-brand">
        <span class="logo">⬛ Kanban</span>
      </div>
      <div class="navbar-links">
        <router-link to="/tableaux">Tableaux</router-link>
        <router-link v-if="authStore.isAdmin" to="/utilisateurs">Utilisateurs</router-link>
        <router-link to="/profil">Mon profil</router-link>
        <div class="user-info">
          <span class="user-name">{{ authStore.user?.prenom }}</span>
          <span :class="['role-badge-nav', authStore.user?.role?.toLowerCase()]">
            {{ authStore.user?.role }}
          </span>
        </div>
        <button class="btn-logout" @click="authStore.logout">Déconnexion</button>
      </div>
    </nav>

    <!-- Toast d'erreur -->
    <transition name="toast">
      <div v-if="toastMessage" class="toast-error" @click="toastMessage = ''">
        ⚠️ {{ toastMessage }}
      </div>
    </transition>

    <main :class="{ 'with-nav': authStore.isAuthenticated }">
      <router-view />
    </main>
  </div>
</template>

<script>
import { ref, onMounted, onUnmounted } from 'vue'
import { useAuthStore } from './stores/auth.js'

export default {
  setup() {
    const authStore = useAuthStore()
    const toastMessage = ref('')
    let toastTimer = null

    function onError(e) {
      toastMessage.value = e.detail
      clearTimeout(toastTimer)
      toastTimer = setTimeout(() => { toastMessage.value = '' }, 5000)
    }

    onMounted(() => window.addEventListener('app-error', onError))
    onUnmounted(() => window.removeEventListener('app-error', onError))

    return { authStore, toastMessage }
  },
}
</script>

<style>
* { margin: 0; padding: 0; box-sizing: border-box; }
body { font-family: 'Segoe UI', Tahoma, Geneva, Verdana, sans-serif; background: #f4f6f9; color: #2c3e50; }

.navbar { position: fixed; top: 0; left: 0; right: 0; height: 60px; background: #1a1f36; display: flex; align-items: center; justify-content: space-between; padding: 0 2rem; z-index: 100; box-shadow: 0 2px 8px rgba(0,0,0,0.2); }
.navbar-brand .logo { color: #fff; font-size: 1.2rem; font-weight: 700; letter-spacing: 1px; }
.navbar-links { display: flex; align-items: center; gap: 1.5rem; }
.navbar-links a { color: #a0aec0; text-decoration: none; font-size: 0.9rem; font-weight: 500; transition: color 0.2s; }
.navbar-links a:hover, .navbar-links a.router-link-active { color: #fff; }
.btn-logout { background: transparent; border: 1px solid #4a5568; color: #a0aec0; padding: 0.4rem 1rem; border-radius: 6px; cursor: pointer; font-size: 0.85rem; transition: all 0.2s; }
.btn-logout:hover { background: #e53e3e; border-color: #e53e3e; color: #fff; }
main.with-nav { padding-top: 60px; }

/* Toast */
.toast-error {
  position: fixed;
  top: 80px;
  right: 20px;
  background: #e53e3e;
  color: #fff;
  padding: 0.8rem 1.5rem;
  border-radius: 8px;
  font-size: 0.9rem;
  font-weight: 500;
  box-shadow: 0 4px 12px rgba(229, 62, 62, 0.4);
  cursor: pointer;
  z-index: 999;
}
.toast-enter-active { animation: slideIn 0.3s ease; }
.toast-leave-active { animation: slideIn 0.3s ease reverse; }
@keyframes slideIn {
  from { opacity: 0; transform: translateX(100px); }
  to { opacity: 1; transform: translateX(0); }
}
</style>