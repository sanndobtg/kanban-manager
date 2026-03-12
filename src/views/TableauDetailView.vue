<template>
  <div class="page">
    <div class="page-header">
      <div>
        <router-link to="/tableaux" class="back-link">← Tableaux</router-link>
        <h1>{{ store.tableauActuel?.nom }}</h1>
      </div>
      <button class="btn-primary" @click="showColonneForm = !showColonneForm">
        + Ajouter une colonne
      </button>
    </div>

    <div v-if="showColonneForm" class="create-form">
      <input v-model="nouvelleColonne" placeholder="Nom de la colonne" />
      <button class="btn-primary" @click="ajouterColonne">Créer</button>
      <button class="btn-secondary" @click="showColonneForm = false">Annuler</button>
    </div>

    <div class="kanban-board">
      <div v-for="colonne in colonnes" :key="colonne.id" class="kanban-colonne">
        <div class="colonne-header">
          <h3>{{ colonne.nom }}</h3>
          <span class="tache-count">{{ tachesParColonne(colonne.id).length }}</span>
        </div>

        <div class="taches-list">
          <div v-for="tache in tachesParColonne(colonne.id)" :key="tache.id" class="tache-card">
            <div class="tache-header">
              <span :class="['priorite-badge', tache.priorite?.toLowerCase()]">
                {{ tache.priorite }}
              </span>
              <button class="btn-close" @click="supprimerTache(tache.id)">✕</button>
            </div>
            <p class="tache-titre">{{ tache.titre }}</p>
            <p v-if="tache.description" class="tache-desc">{{ tache.description }}</p>
            <p v-if="tache.dateLimit" class="tache-date">📅 {{ tache.dateLimit }}</p>
          </div>
        </div>

        <form @submit.prevent="ajouterTache(colonne.id)" class="tache-form">
          <input v-model="nouvelleTache.titre" placeholder="Titre de la tâche" required />
          <input v-model="nouvelleTache.description" placeholder="Description" />
          <div class="tache-form-row">
            <input v-model="nouvelleTache.dateLimit" type="date" />
            <select v-model="nouvelleTache.priorite">
              <option value="BASSE">Basse</option>
              <option value="MOYENNE">Moyenne</option>
              <option value="HAUTE">Haute</option>
            </select>
          </div>
          <button type="submit" class="btn-add-tache">+ Ajouter</button>
        </form>
      </div>
    </div>
  </div>
</template>

<script>
import { ref, onMounted } from 'vue'
import { useRoute } from 'vue-router'
import { useTableauxStore } from '../stores/tableaux.js'
import { colonneService } from '../services/colonneService.js'
import { useTachesStore } from '../stores/taches.js'
import { useAuthStore } from '../stores/auth.js'

export default {
  setup() {
    const route            = useRoute()
    const store            = useTableauxStore()
    const tachesStore      = useTachesStore()
    const authStore        = useAuthStore()
    const colonnes         = ref([])
    const showColonneForm  = ref(false)
    const nouvelleColonne  = ref('')
    const nouvelleTache    = ref({ titre: '', description: '', dateLimit: '', priorite: 'MOYENNE' })

    onMounted(async () => {
      await store.fetchById(route.params.id)
      const res = await colonneService.getByTableau(route.params.id)
      colonnes.value = res.data
      for (const colonne of colonnes.value) {
        await tachesStore.fetchByColonne(colonne.id)
      }
    })

    function tachesParColonne(idColonne) {
      return tachesStore.taches.filter(t => t.idColonne === idColonne)
    }

    async function ajouterColonne() {
      if (!nouvelleColonne.value.trim()) return
      const res = await colonneService.create({
        nom: nouvelleColonne.value,
        idTableau: Number(route.params.id)
      })
      colonnes.value.push(res.data)
      nouvelleColonne.value = ''
      showColonneForm.value = false
    }

    async function ajouterTache(idColonne) {
      await tachesStore.create({
        ...nouvelleTache.value,
        idColonne,
        idUtilisateur: authStore.user.id,
      })
      nouvelleTache.value = { titre: '', description: '', dateLimit: '', priorite: 'MOYENNE' }
    }

    async function supprimerTache(id) {
      await tachesStore.delete(id)
    }

    return {
      store, colonnes, showColonneForm, nouvelleColonne,
      nouvelleTache, tachesParColonne, ajouterColonne,
      ajouterTache, supprimerTache
    }
  }
}
</script>

<style scoped>
.page {
  padding: 2rem;
  max-width: 100%;
}

.page-header {
  display: flex;
  justify-content: space-between;
  align-items: flex-end;
  margin-bottom: 1.5rem;
}

.back-link {
  color: #718096;
  text-decoration: none;
  font-size: 0.85rem;
  display: block;
  margin-bottom: 0.3rem;
  transition: color 0.2s;
}

.back-link:hover { color: #1a1f36; }

.page-header h1 {
  font-size: 1.5rem;
  font-weight: 700;
  color: #1a1f36;
}

.create-form {
  display: flex;
  gap: 0.8rem;
  margin-bottom: 1.5rem;
  background: #fff;
  padding: 1rem;
  border-radius: 10px;
  box-shadow: 0 2px 8px rgba(0,0,0,0.06);
}

.create-form input {
  flex: 1;
  padding: 0.65rem 1rem;
  border: 1.5px solid #e2e8f0;
  border-radius: 8px;
  outline: none;
  font-size: 0.9rem;
}

.kanban-board {
  display: flex;
  gap: 1.2rem;
  overflow-x: auto;
  padding-bottom: 1rem;
  align-items: flex-start;
}

.kanban-colonne {
  background: #f0f2f5;
  border-radius: 10px;
  min-width: 280px;
  max-width: 280px;
  padding: 1rem;
}

.colonne-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 1rem;
}

.colonne-header h3 {
  font-size: 0.95rem;
  font-weight: 700;
  color: #1a1f36;
  text-transform: uppercase;
  letter-spacing: 0.5px;
}

.tache-count {
  background: #1a1f36;
  color: #fff;
  border-radius: 12px;
  padding: 0.15rem 0.6rem;
  font-size: 0.75rem;
  font-weight: 600;
}

.taches-list {
  display: flex;
  flex-direction: column;
  gap: 0.8rem;
  margin-bottom: 1rem;
}

.tache-card {
  background: #fff;
  border-radius: 8px;
  padding: 0.9rem;
  box-shadow: 0 1px 4px rgba(0,0,0,0.08);
  transition: box-shadow 0.2s;
}

.tache-card:hover {
  box-shadow: 0 4px 12px rgba(0,0,0,0.12);
}

.tache-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 0.5rem;
}

.priorite-badge {
  font-size: 0.7rem;
  font-weight: 700;
  padding: 0.2rem 0.6rem;
  border-radius: 20px;
  text-transform: uppercase;
  letter-spacing: 0.5px;
}

.priorite-badge.haute   { background: #fff5f5; color: #e53e3e; }
.priorite-badge.moyenne { background: #fffaf0; color: #d69e2e; }
.priorite-badge.basse   { background: #f0fff4; color: #38a169; }

.tache-titre {
  font-size: 0.9rem;
  font-weight: 600;
  color: #2d3748;
  margin-bottom: 0.3rem;
}

.tache-desc {
  font-size: 0.82rem;
  color: #718096;
  margin-bottom: 0.3rem;
}

.tache-date {
  font-size: 0.78rem;
  color: #a0aec0;
}

.btn-close {
  background: transparent;
  border: none;
  color: #cbd5e0;
  cursor: pointer;
  font-size: 0.8rem;
  transition: color 0.2s;
  padding: 0;
}

.btn-close:hover { color: #e53e3e; }

.tache-form {
  display: flex;
  flex-direction: column;
  gap: 0.5rem;
  background: #fff;
  border-radius: 8px;
  padding: 0.8rem;
  border: 1.5px dashed #cbd5e0;
}

.tache-form input,
.tache-form select {
  padding: 0.55rem 0.8rem;
  border: 1.5px solid #e2e8f0;
  border-radius: 6px;
  font-size: 0.85rem;
  outline: none;
  background: #fff;
}

.tache-form input:focus,
.tache-form select:focus {
  border-color: #4c6ef5;
}

.tache-form-row {
  display: grid;
  grid-template-columns: 1fr 1fr;
  gap: 0.5rem;
}

.btn-add-tache {
  background: #1a1f36;
  color: #fff;
  border: none;
  padding: 0.55rem;
  border-radius: 6px;
  font-size: 0.85rem;
  font-weight: 600;
  cursor: pointer;
  transition: background 0.2s;
}

.btn-add-tache:hover { background: #2d3561; }

.btn-primary {
  background: #1a1f36;
  color: #fff;
  border: none;
  padding: 0.65rem 1.2rem;
  border-radius: 8px;
  font-size: 0.9rem;
  font-weight: 600;
  cursor: pointer;
  transition: background 0.2s;
}

.btn-primary:hover { background: #2d3561; }

.btn-secondary {
  background: transparent;
  color: #4a5568;
  border: 1.5px solid #e2e8f0;
  padding: 0.65rem 1.2rem;
  border-radius: 8px;
  font-size: 0.9rem;
  cursor: pointer;
}
</style>