<template>
  <div class="auth-page">
    <div class="auth-card">
      <div class="auth-header">
        <h1>⬛ Kanban</h1>
        <p>Connectez-vous à votre espace</p>
      </div>

      <form @submit.prevent="handleLogin" class="auth-form">
        <div class="form-group">
          <label>Email</label>
          <input v-model="email" type="email" placeholder="votre@email.com" required />
        </div>
        <div class="form-group">
          <label>Mot de passe</label>
          <input v-model="mot_de_passe" type="password" placeholder="••••••••" required />
        </div>
        <p v-if="erreur" class="error-msg">{{ erreur }}</p>
        <button type="submit" class="btn-primary">Se connecter</button>
      </form>

      <div class="auth-footer">
        Pas encore de compte ?
        <router-link to="/register">Créer un compte</router-link>
      </div>
    </div>
  </div>
</template>

<script>
import { ref } from 'vue'
import { useRouter } from 'vue-router'
import { useAuthStore } from '../stores/auth.js'

export default {
  setup() {
    const email        = ref('')
    const mot_de_passe = ref('')
    const erreur       = ref('')
    const router       = useRouter()
    const authStore    = useAuthStore()

    async function handleLogin() {
      try {
        await authStore.login(email.value, mot_de_passe.value)
        router.push('/tableaux')
      } catch (e) {
        erreur.value = 'Email ou mot de passe incorrect'
      }
    }

    return { email, mot_de_passe, erreur, handleLogin }
  }
}
</script>

<style scoped>
.auth-page {
  min-height: 100vh;
  background: linear-gradient(135deg, #1a1f36 0%, #2d3561 100%);
  display: flex;
  align-items: center;
  justify-content: center;
}

.auth-card {
  background: #fff;
  border-radius: 12px;
  padding: 2.5rem;
  width: 100%;
  max-width: 420px;
  box-shadow: 0 20px 60px rgba(0,0,0,0.3);
}

.auth-header {
  text-align: center;
  margin-bottom: 2rem;
}

.auth-header h1 {
  font-size: 1.8rem;
  font-weight: 800;
  color: #1a1f36;
  margin-bottom: 0.5rem;
}

.auth-header p {
  color: #718096;
  font-size: 0.9rem;
}

.auth-form {
  display: flex;
  flex-direction: column;
  gap: 1.2rem;
}

.form-group {
  display: flex;
  flex-direction: column;
  gap: 0.4rem;
}

.form-group label {
  font-size: 0.85rem;
  font-weight: 600;
  color: #4a5568;
}

.form-group input {
  padding: 0.75rem 1rem;
  border: 1.5px solid #e2e8f0;
  border-radius: 8px;
  font-size: 0.95rem;
  transition: border-color 0.2s;
  outline: none;
}

.form-group input:focus {
  border-color: #4c6ef5;
}

.error-msg {
  color: #e53e3e;
  font-size: 0.85rem;
  text-align: center;
}

.btn-primary {
  background: #1a1f36;
  color: #fff;
  border: none;
  padding: 0.85rem;
  border-radius: 8px;
  font-size: 1rem;
  font-weight: 600;
  cursor: pointer;
  transition: background 0.2s;
  margin-top: 0.5rem;
}

.btn-primary:hover {
  background: #2d3561;
}

.auth-footer {
  text-align: center;
  margin-top: 1.5rem;
  font-size: 0.88rem;
  color: #718096;
}

.auth-footer a {
  color: #4c6ef5;
  font-weight: 600;
  text-decoration: none;
  margin-left: 0.3rem;
}
</style>