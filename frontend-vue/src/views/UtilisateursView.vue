<template>
  <div class="page">
    <div v-if="!authStore.isAdmin" class="access-denied">
      <h2>🔒 Accès refusé</h2>
      <p>Cette page est réservée aux administrateurs.</p>
      <router-link to="/tableaux" class="btn-primary">Retour aux tableaux</router-link>
    </div>

    <template v-else>
      <div class="page-header">
        <h1>Administration</h1>
      </div>

      <!-- Statistiques globales -->
      <div class="stats-grid">
        <div class="stat-card">
          <span class="stat-icon">📋</span>
          <div class="stat-info">
            <span class="stat-value">{{ stats.nombreTableaux }}</span>
            <span class="stat-label">Tableaux</span>
          </div>
        </div>
        <div class="stat-card">
          <span class="stat-icon">✅</span>
          <div class="stat-info">
            <span class="stat-value">{{ stats.nombreTaches }}</span>
            <span class="stat-label">Tâches</span>
          </div>
        </div>
        <div class="stat-card">
          <span class="stat-icon">👥</span>
          <div class="stat-info">
            <span class="stat-value">{{ stats.nombreUtilisateurs }}</span>
            <span class="stat-label">Utilisateurs</span>
          </div>
        </div>
        <div class="stat-card">
          <span class="stat-icon">💬</span>
          <div class="stat-info">
            <span class="stat-value">{{ stats.nombreCommentaires }}</span>
            <span class="stat-label">Commentaires</span>
          </div>
        </div>
      </div>

      <!-- Liste des utilisateurs -->
      <div class="section-header">
        <h2>Utilisateurs</h2>
        <span class="count-badge">{{ utilisateurs.length }} membres</span>
      </div>

      <div class="table-wrapper">
        <table class="data-table">
          <thead>
            <tr>
              <th>#</th>
              <th>Nom</th>
              <th>Prénom</th>
              <th>Email</th>
              <th>Rôle</th>
              <th>Actions</th>
            </tr>
          </thead>
          <tbody>
            <tr v-for="u in utilisateurs" :key="u.id">
              <td class="td-id">{{ u.id }}</td>
              <td><strong>{{ u.nom }}</strong></td>
              <td>{{ u.prenom }}</td>
              <td class="td-email">{{ u.email }}</td>
              <td>
                <span :class="['role-badge', u.role?.toLowerCase()]">{{ u.role }}</span>
              </td>
              <td>
                <button class="btn-danger" @click="supprimer(u.id)">Supprimer</button>
              </td>
            </tr>
          </tbody>
        </table>
      </div>
    </template>
  </div>
</template>

<script>
import { ref, onMounted } from 'vue'
import { utilisateurService } from '../services/utilisateurService.js'
import { statsService } from '../services/statsService.js'
import { useAuthStore } from '../stores/auth.js'

export default {
  setup() {
    const utilisateurs = ref([])
    const authStore = useAuthStore()
    const stats = ref({
      nombreTableaux: 0,
      nombreTaches: 0,
      nombreUtilisateurs: 0,
      nombreCommentaires: 0,
    })

    onMounted(async () => {
      if (!authStore.isAdmin) return
      const [resUsers, resStats] = await Promise.all([
        utilisateurService.getAll(),
        statsService.getStats(),
      ])
      utilisateurs.value = resUsers.data
      stats.value = resStats.data
    })

    async function supprimer(id) {
      await utilisateurService.delete(id)
      utilisateurs.value = utilisateurs.value.filter((u) => u.id !== id)
      stats.value.nombreUtilisateurs--
    }

    return { utilisateurs, authStore, stats, supprimer }
  },
}
</script>

<style scoped>
.page { padding: 2rem; max-width: 1100px; margin: 0 auto; }
.page-header { margin-bottom: 1.5rem; }
.page-header h1 { font-size: 1.6rem; font-weight: 700; color: #1a1f36; }

/* Stats */
.stats-grid {
  display: grid;
  grid-template-columns: repeat(4, 1fr);
  gap: 1rem;
  margin-bottom: 2rem;
}
.stat-card {
  background: #fff;
  border-radius: 12px;
  padding: 1.2rem;
  display: flex;
  align-items: center;
  gap: 1rem;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.06);
}
.stat-icon { font-size: 2rem; }
.stat-info { display: flex; flex-direction: column; }
.stat-value { font-size: 1.8rem; font-weight: 700; color: #1a1f36; line-height: 1; }
.stat-label { font-size: 0.82rem; color: #718096; font-weight: 500; margin-top: 0.2rem; }

/* Section utilisateurs */
.section-header { display: flex; align-items: center; gap: 1rem; margin-bottom: 1rem; }
.section-header h2 { font-size: 1.2rem; font-weight: 700; color: #1a1f36; }
.count-badge { background: #edf2ff; color: #4c6ef5; font-size: 0.8rem; font-weight: 600; padding: 0.3rem 0.8rem; border-radius: 20px; }

/* Table */
.table-wrapper { background: #fff; border-radius: 12px; box-shadow: 0 2px 8px rgba(0, 0, 0, 0.06); overflow: hidden; }
.data-table { width: 100%; border-collapse: collapse; }
.data-table thead { background: #f7fafc; border-bottom: 2px solid #e2e8f0; }
.data-table th { padding: 1rem 1.2rem; text-align: left; font-size: 0.78rem; font-weight: 700; color: #718096; text-transform: uppercase; letter-spacing: 0.5px; }
.data-table td { padding: 1rem 1.2rem; font-size: 0.9rem; color: #2d3748; border-bottom: 1px solid #f0f2f5; }
.data-table tr:last-child td { border-bottom: none; }
.data-table tr:hover td { background: #f7fafc; }
.td-id { color: #a0aec0; font-size: 0.85rem; }
.td-email { color: #718096; }
.role-badge { font-size: 0.75rem; font-weight: 700; padding: 0.25rem 0.7rem; border-radius: 20px; text-transform: uppercase; }
.role-badge.admin { background: #fef3c7; color: #d97706; }
.role-badge.utilisateur { background: #e0f2fe; color: #0369a1; }
.btn-danger { background: transparent; color: #e53e3e; border: 1.5px solid #fed7d7; padding: 0.4rem 0.9rem; border-radius: 6px; font-size: 0.82rem; font-weight: 600; cursor: pointer; transition: all 0.2s; }
.btn-danger:hover { background: #e53e3e; color: #fff; border-color: #e53e3e; }
</style>