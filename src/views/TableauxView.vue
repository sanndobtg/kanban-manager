<template>
  <div class="page">
    <div class="page-header">
      <h1>Mes Tableaux</h1>
      <button class="btn-primary" @click="showForm = !showForm">+ Nouveau tableau</button>
    </div>

    <div v-if="showForm" class="create-form">
      <input v-model="nouveauNom" placeholder="Nom du tableau" />
      <button class="btn-primary" @click="creerTableau">Créer</button>
      <button class="btn-secondary" @click="showForm = false">Annuler</button>
    </div>

    <div class="tableaux-grid">
      <div v-for="tableau in store.tableaux" :key="tableau.id" class="tableau-card">
        <div class="tableau-card-body">
          <h3>{{ tableau.nom }}</h3>
        </div>
        <div class="tableau-card-footer">
          <router-link :to="`/tableaux/${tableau.id}`" class="btn-link">Ouvrir →</router-link>
          <button class="btn-danger" @click="store.delete(tableau.id)">Supprimer</button>
        </div>
      </div>
    </div>
  </div>
</template>

<script>
import { ref, onMounted } from 'vue'
import { useTableauxStore } from '../stores/tableaux.js'

export default {
  setup() {
    const store      = useTableauxStore()
    const nouveauNom = ref('')
    const showForm   = ref(false)

    onMounted(() => store.fetchAll())

    async function creerTableau() {
      if (!nouveauNom.value.trim()) return
      await store.create({ nom: nouveauNom.value })
      nouveauNom.value = ''
      showForm.value   = false
    }

    return { store, nouveauNom, showForm, creerTableau }
  }
}
</script>

<style scoped>
.page {
  padding: 2rem;
  max-width: 1200px;
  margin: 0 auto;
}

.page-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 2rem;
}

.page-header h1 {
  font-size: 1.6rem;
  font-weight: 700;
  color: #1a1f36;
}

.create-form {
  display: flex;
  gap: 0.8rem;
  margin-bottom: 2rem;
  background: #fff;
  padding: 1.2rem;
  border-radius: 10px;
  box-shadow: 0 2px 8px rgba(0,0,0,0.06);
}

.create-form input {
  flex: 1;
  padding: 0.7rem 1rem;
  border: 1.5px solid #e2e8f0;
  border-radius: 8px;
  font-size: 0.95rem;
  outline: none;
}

.create-form input:focus {
  border-color: #4c6ef5;
}

.tableaux-grid {
  display: grid;
  grid-template-columns: repeat(auto-fill, minmax(260px, 1fr));
  gap: 1.5rem;
}

.tableau-card {
  background: #fff;
  border-radius: 10px;
  box-shadow: 0 2px 8px rgba(0,0,0,0.06);
  overflow: hidden;
  transition: transform 0.2s, box-shadow 0.2s;
  border-top: 4px solid #1a1f36;
}

.tableau-card:hover {
  transform: translateY(-3px);
  box-shadow: 0 8px 24px rgba(0,0,0,0.12);
}

.tableau-card-body {
  padding: 1.5rem;
}

.tableau-card-body h3 {
  font-size: 1.1rem;
  font-weight: 600;
  color: #1a1f36;
}

.tableau-card-footer {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 0.8rem 1.5rem;
  background: #f7fafc;
  border-top: 1px solid #e2e8f0;
}

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
  transition: all 0.2s;
}

.btn-secondary:hover { background: #f7fafc; }

.btn-danger {
  background: transparent;
  color: #e53e3e;
  border: none;
  font-size: 0.85rem;
  cursor: pointer;
  font-weight: 500;
  transition: color 0.2s;
}

.btn-danger:hover { color: #c53030; }

.btn-link {
  color: #4c6ef5;
  text-decoration: none;
  font-size: 0.9rem;
  font-weight: 600;
  transition: color 0.2s;
}

.btn-link:hover { color: #2d3561; }
</style>