<template>
  <div class="page">
    <div class="page-header">
      <div>
        <router-link to="/tableaux" class="back-link">← Tableaux</router-link>
        <h1>{{ store.tableauActuel?.nom }}</h1>
      </div>
      <div class="header-actions">
        <button class="btn-secondary" @click="showMembres = !showMembres">
          👥 Membres ({{ store.tableauActuel?.utilisateurs?.length || 0 }})
        </button>
        <button class="btn-primary" @click="showColonneForm = !showColonneForm">
          + Colonne
        </button>
      </div>
    </div>

    <!-- Panel membres du tableau -->
    <div v-if="showMembres" class="membres-panel">
      <h3>Membres du tableau</h3>
      <div class="membres-liste">
        <div
          v-for="membre in store.tableauActuel?.utilisateurs"
          :key="membre.id"
          class="membre-item"
        >
          <div class="membre-info">
            <span class="membre-avatar"
              >{{ membre.prenom[0] }}{{ membre.nom[0] }}</span
            >
            <span>{{ membre.prenom }} {{ membre.nom }}</span>
            <span :class="['role-badge', membre.role?.toLowerCase()]">{{
              membre.role
            }}</span>
          </div>
          <button
            v-if="
              store.tableauActuel?.idCreateur === authStore.user.id &&
              membre.id !== authStore.user.id
            "
            class="btn-danger-sm"
            @click="retirerMembre(membre.id)"
          >
            Retirer
          </button>
        </div>
      </div>
      <div class="ajouter-membre">
        <h4>Ajouter un membre</h4>
        <div class="ajouter-membre-form">
          <select v-model="membreSelectionne">
            <option value="">-- Choisir un utilisateur --</option>
            <option
              v-for="u in utilisateursDisponibles"
              :key="u.id"
              :value="u.id"
            >
              {{ u.prenom }} {{ u.nom }} ({{ u.email }})
            </option>
          </select>
          <button
            class="btn-primary"
            :disabled="!membreSelectionne"
            @click="ajouterMembre"
          >
            Ajouter
          </button>
        </div>
      </div>
    </div>

    <!-- Formulaire nouvelle colonne -->
    <div v-if="showColonneForm" class="create-form">
      <input v-model="nouvelleColonne" placeholder="Nom de la colonne" />
      <button class="btn-primary" @click="ajouterColonne">Créer</button>
      <button class="btn-secondary" @click="showColonneForm = false">
        Annuler
      </button>
    </div>

    <!-- Board Kanban -->
    <div class="kanban-board">
      <div
        v-for="colonne in colonnes"
        :key="colonne.id"
        class="kanban-colonne"
        @dragover.prevent
        @drop="onDrop($event, colonne.id)"
      >
        <div class="colonne-header">
          <h3>{{ colonne.nom }}</h3>
          <span class="tache-count">
            {{ (tachesStore.tachesParColonne[colonne.id] || []).length }}
          </span>
        </div>

        <div class="taches-list">
          <div
            v-for="tache in tachesStore.tachesParColonne[colonne.id] || []"
            :key="tache.id"
            class="tache-card"
            draggable="true"
            @dragstart="onDragStart($event, tache, colonne.id)"
            @dblclick="ouvrirTache(tache)"
          >
            <div class="tache-header">
              <span :class="['priorite-badge', tache.priorite?.toLowerCase()]">
                {{ tache.priorite }}
              </span>
              <button
                v-if="tache.idCreateur === authStore.user.id"
                class="btn-close"
                @click.stop="supprimerTache(tache.id, colonne.id)"
              >
                ✕
              </button>
            </div>
            <p class="tache-titre">{{ tache.titre }}</p>
            <p v-if="tache.description" class="tache-desc">
              {{ tache.description }}
            </p>
            <p v-if="tache.dateLimit" class="tache-date">
              📅 {{ tache.dateLimit }}
            </p>
            <p class="tache-hint">Double-clic pour gérer</p>
          </div>
        </div>

        <button class="btn-add-tache-toggle" @click="toggleForm(colonne.id)">
          + Ajouter une tâche
        </button>

        <form
          v-if="formOuvert === colonne.id"
          @submit.prevent="ajouterTache(colonne.id)"
          class="tache-form"
        >
          <input
            v-model="formulaires[colonne.id].titre"
            placeholder="Titre"
            required
          />
          <input
            v-model="formulaires[colonne.id].description"
            placeholder="Description"
          />
          <div class="tache-form-row">
            <input v-model="formulaires[colonne.id].dateLimit" type="date" />
            <select v-model="formulaires[colonne.id].priorite">
              <option value="BASSE">Basse</option>
              <option value="MOYENNE">Moyenne</option>
              <option value="HAUTE">Haute</option>
            </select>
          </div>
          <div class="tache-form-actions">
            <button type="submit" class="btn-primary">Ajouter</button>
            <button
              type="button"
              class="btn-secondary"
              @click="formOuvert = null"
            >
              Annuler
            </button>
          </div>
        </form>
      </div>
    </div>

    <!-- Modal détail tâche -->
    <div
      v-if="tacheSelectionnee"
      class="modal-overlay"
      @click.self="tacheSelectionnee = null"
    >
      <div class="modal">
        <div class="modal-header">
          <h2>{{ tacheSelectionnee.titre }}</h2>
          <button class="btn-close-modal" @click="tacheSelectionnee = null">
            ✕
          </button>
        </div>

        <div class="modal-body">
          <p class="modal-desc">{{ tacheSelectionnee.description }}</p>

          <div class="modal-meta">
            <span
              :class="[
                'priorite-badge',
                tacheSelectionnee.priorite?.toLowerCase(),
              ]"
            >
              {{ tacheSelectionnee.priorite }}
            </span>
            <span v-if="tacheSelectionnee.dateLimit" class="tache-date">
              📅 {{ tacheSelectionnee.dateLimit }}
            </span>
          </div>

          <!-- Membres assignés à la tâche -->
          <div class="modal-section">
            <h4>Membres assignés</h4>
            <div class="membres-assignes">
              <div
                v-for="idU in tacheSelectionnee.idUtilisateurs"
                :key="idU"
                class="membre-tag"
              >
                <span>{{ getNomUtilisateur(idU) }}</span>
                <button
                  v-if="tacheSelectionnee.idCreateur === authStore.user.id"
                  @click="retirerUtilisateurTache(tacheSelectionnee, idU)"
                >
                  ✕
                </button>
              </div>
              <p
                v-if="!tacheSelectionnee.idUtilisateurs?.length"
                class="empty-msg"
              >
                Aucun membre assigné
              </p>
            </div>
          </div>

          <!-- Ajouter un membre à la tâche -->
          <div class="modal-section">
            <h4>Ajouter un membre à la tâche</h4>
            <div class="ajouter-membre-form">
              <select v-model="utilisateurATache">
                <option value="">-- Choisir --</option>
                <option
                  v-for="u in utilisateursDispo(tacheSelectionnee)"
                  :key="u.id"
                  :value="u.id"
                >
                  {{ u.prenom }} {{ u.nom }}
                </option>
              </select>
              <button
                class="btn-primary"
                :disabled="!utilisateurATache"
                @click="ajouterUtilisateurTache(tacheSelectionnee)"
              >
                Ajouter
              </button>
            </div>
          </div>

          <!-- Commentaires -->
          <div
            v-if="
              tacheSelectionnee.idUtilisateurs?.includes(authStore.user.id) ||
              tacheSelectionnee.idCreateur === authStore.user.id
            "
            class="modal-section"
          >
            <h4>Commentaires ({{ commentaires.length }})</h4>

            <div class="commentaires-liste">
              <div
                v-for="c in commentaires"
                :key="c.id"
                class="commentaire-item"
              >
                <div class="commentaire-header">
                  <span class="commentaire-auteur">
                    {{ getNomUtilisateurById(c.idUtilisateur) }}
                  </span>
                  <span class="commentaire-date">
                    {{ new Date(c.dateCreation).toLocaleString("fr-FR") }}
                  </span>
                  <button
                    v-if="c.idUtilisateur === authStore.user.id"
                    class="btn-close"
                    @click="supprimerCommentaire(c.id)"
                  >
                    ✕
                  </button>
                </div>
                <p class="commentaire-contenu">{{ c.contenu }}</p>
                <div v-if="c.piecesJointes?.length" class="pieces-jointes">
                <div v-for="pj in c.piecesJointes" :key="pj.id" class="pj-item" @click="telechargerFichier(pj)">
                  <span>📎 {{ pj.nomFichier }}</span>
                  <span class="pj-size">{{ formatTaille(pj.taille) }}</span>
                </div>
              </div>
              </div>
              <p v-if="!commentaires.length" class="empty-msg">
                Aucun commentaire
              </p>
            </div>

           <div class="commentaire-form-wrap">
  <div v-if="fichiersSelectionnes.length" class="fichiers-preview">
    <div v-for="(f, i) in fichiersSelectionnes" :key="i" class="fichier-tag">
      📎 {{ f.name }} ({{ formatTaille(f.size) }})
      <button class="btn-close" @click="retirerFichier(i)">✕</button>
    </div>
  </div>
  <div class="commentaire-form">
    <input v-model="nouveauCommentaire" placeholder="Ajouter un commentaire..."
           @keyup.enter="ajouterCommentaire" :disabled="envoyEnCours" />
    <label class="btn-attach" title="Joindre un fichier">
      📎<input type="file" multiple hidden ref="fileInput" @change="onFichiersChange" />
    </label>
    <button class="btn-primary"
            :disabled="(!nouveauCommentaire.trim() && !fichiersSelectionnes.length) || envoyEnCours"
            @click="ajouterCommentaire">
      {{ envoyEnCours ? '...' : 'Envoyer' }}
    </button>
  </div>
</div>
          </div>

          <div v-else class="modal-section">
            <p class="empty-msg">
              🔒 Vous devez être assigné à cette tâche pour voir les
              commentaires.
            </p>
          </div>
        </div>
      </div>
    </div>
  </div>
</template>

<script>
import { ref, onMounted, reactive, computed } from "vue";
import { useRoute } from "vue-router";
import { useTableauxStore } from "../stores/tableaux.js";
import { colonneService } from "../services/colonneService.js";
import { useTachesStore } from "../stores/taches.js";
import { useAuthStore } from "../stores/auth.js";
import { utilisateurService } from "../services/utilisateurService.js";
import { tableauService } from "../services/tableauService.js";
import { tacheService } from "../services/tacheService.js";
import { commentaireService } from "../services/commentaireService.js";

import { fichierService } from "../services/fichierService.js";

export default {
  setup() {
    const route = useRoute();
    const store = useTableauxStore();
    const tachesStore = useTachesStore();
    const authStore = useAuthStore();
    const colonnes = ref([]);
    const showColonneForm = ref(false);
    const showMembres = ref(false);
    const nouvelleColonne = ref("");
    const formOuvert = ref(null);
    const formulaires = reactive({});
    const dragData = ref(null);
    const tousUtilisateurs = ref([]);
    const membreSelectionne = ref("");
    const tacheSelectionnee = ref(null);
    const utilisateurATache = ref("");
    const commentaires = ref([]);
    const nouveauCommentaire = ref("");

    const fichiersSelectionnes = ref([]);
    const envoyEnCours = ref(false);
    const fileInput = ref(null);

    const utilisateursDisponibles = computed(() => {
      const membreIds = (store.tableauActuel?.utilisateurs || []).map(
        (u) => u.id,
      );
      return tousUtilisateurs.value.filter((u) => !membreIds.includes(u.id));
    });

    onMounted(async () => {
      await store.fetchById(route.params.id);
      const [resColonnes, resUtilisateurs] = await Promise.all([
        colonneService.getByTableau(route.params.id),
        utilisateurService.getAll(),
      ]);
      colonnes.value = resColonnes.data;
      tousUtilisateurs.value = resUtilisateurs.data;

      for (const colonne of colonnes.value) {
        formulaires[colonne.id] = videFormulaire();
        await tachesStore.fetchByColonne(colonne.id);
      }
    });

    function videFormulaire() {
      return { titre: "", description: "", dateLimit: "", priorite: "MOYENNE" };
    }

    function toggleForm(idColonne) {
      formOuvert.value = formOuvert.value === idColonne ? null : idColonne;
      if (!formulaires[idColonne]) formulaires[idColonne] = videFormulaire();
    }

    async function ajouterColonne() {
      if (!nouvelleColonne.value.trim()) return;
      const res = await colonneService.create({
        nom: nouvelleColonne.value,
        idTableau: Number(route.params.id),
      });
      const col = res.data;
      colonnes.value.push(col);
      formulaires[col.id] = videFormulaire();
      nouvelleColonne.value = "";
      showColonneForm.value = false;
    }

    async function ajouterTache(idColonne) {
      const form = formulaires[idColonne];
      if (!form.titre.trim()) return;
      await tachesStore.create({
        ...form,
        idColonne,
        idUtilisateurs: [],
      });
      formulaires[idColonne] = videFormulaire();
      formOuvert.value = null;
    }

    async function supprimerTache(id, idColonne) {
      await tachesStore.delete(id, idColonne);
    }

    // Membres du tableau
    async function ajouterMembre() {
      if (!membreSelectionne.value) return;
      await tableauService.ajouterMembre(
        route.params.id,
        membreSelectionne.value,
      );
      await store.fetchById(route.params.id);
      membreSelectionne.value = "";
    }

    async function retirerMembre(idUtilisateur) {
      await tableauService.retirerMembre(route.params.id, idUtilisateur);
      await store.fetchById(route.params.id);
    }

    // Modal tâche
    async function ouvrirTache(tache) {
      tacheSelectionnee.value = { ...tache };
      utilisateurATache.value = "";
      nouveauCommentaire.value = "";
      commentaires.value = [];

      const estAssigne = tache.idUtilisateurs?.includes(authStore.user.id);
      const estCreateur = tache.idCreateur === authStore.user.id;

      if (estAssigne || estCreateur) {
        try {
          const res = await commentaireService.getByTache(tache.id);
          commentaires.value = res.data.data ?? res.data;
        } catch (e) {
          commentaires.value = [];
        }
      }
    }

    function getNomUtilisateur(id) {
      const u = tousUtilisateurs.value.find((u) => u.id === id);
      return u ? `${u.prenom} ${u.nom}` : `Utilisateur #${id}`;
    }

    function getNomUtilisateurById(id) {
      const u = tousUtilisateurs.value.find((u) => u.id === id);
      return u ? `${u.prenom} ${u.nom}` : `Utilisateur #${id}`;
    }

    function utilisateursDispo(tache) {
      return tousUtilisateurs.value.filter(
        (u) => !tache.idUtilisateurs?.includes(u.id),
      );
    }

    async function ajouterUtilisateurTache(tache) {
      if (!utilisateurATache.value) return;
      await tacheService.ajouterUtilisateur(tache.id, utilisateurATache.value);
      tache.idUtilisateurs = [
        ...(tache.idUtilisateurs || []),
        utilisateurATache.value,
      ];
      const tacheDansStore = tachesStore.tachesParColonne[
        tache.idColonne
      ]?.find((t) => t.id === tache.id);
      if (tacheDansStore)
        tacheDansStore.idUtilisateurs = [...tache.idUtilisateurs];
      utilisateurATache.value = "";
    }

    async function retirerUtilisateurTache(tache, idUtilisateur) {
      await tacheService.retirerUtilisateur(tache.id, idUtilisateur);
      tache.idUtilisateurs = tache.idUtilisateurs.filter(
        (id) => id !== idUtilisateur,
      );
      const tacheDansStore = tachesStore.tachesParColonne[
        tache.idColonne
      ]?.find((t) => t.id === tache.id);
      if (tacheDansStore)
        tacheDansStore.idUtilisateurs = [...tache.idUtilisateurs];
    }

    // Commentaires
    async function ajouterCommentaire() {
      if (!nouveauCommentaire.value.trim() && !fichiersSelectionnes.value.length) return;
      envoyEnCours.value = true;
      try {
          const res = await commentaireService.create(
            tacheSelectionnee.value.id,
            nouveauCommentaire.value,
            fichiersSelectionnes.value
          );
          commentaires.value.push(res.data.data ?? res.data);
          nouveauCommentaire.value = "";
          fichiersSelectionnes.value = [];
      } catch (e) {
          console.error("Erreur:", e);
      } finally {
          envoyEnCours.value = false;
      }
    }

    async function supprimerCommentaire(id) {
      await commentaireService.delete(id);
      commentaires.value = commentaires.value.filter((c) => c.id !== id);
    }

    // Drag & drop
    function onDragStart(event, tache, idColonneSource) {
      dragData.value = { tache, idColonneSource };
      event.dataTransfer.effectAllowed = "move";
    }

    async function onDrop(event, idColonneCible) {
      if (!dragData.value) return;
      const { tache, idColonneSource } = dragData.value;
      if (idColonneSource === idColonneCible) return;
      await tachesStore.deplacerTache(tache, idColonneSource, idColonneCible);
      dragData.value = null;
    }

    function onFichiersChange(e) {
  const files = Array.from(e.target.files);
  for (const f of files) {
    if (f.size > 10 * 1024 * 1024) { alert(`${f.name} dépasse 10 Mo`); return; }
  }
  fichiersSelectionnes.value.push(...files);
}

function retirerFichier(i) { fichiersSelectionnes.value.splice(i, 1); }

function formatTaille(o) {
  if (o < 1024) return o + " o";
  if (o < 1048576) return (o / 1024).toFixed(1) + " Ko";
  return (o / 1048576).toFixed(1) + " Mo";
}

async function telechargerFichier(pj) {
  try { await fichierService.download(pj); } catch (e) { console.error(e); }
}


    return {
      store,
      colonnes,
      tachesStore,
      authStore,
      showColonneForm,
      showMembres,
      nouvelleColonne,
      formOuvert,
      formulaires,
      membreSelectionne,
      utilisateursDisponibles,
      tacheSelectionnee,
      utilisateurATache,
      commentaires,
      nouveauCommentaire,
      fichiersSelectionnes, 
      envoyEnCours, 
      fileInput,
      toggleForm,
      ajouterColonne,
      ajouterTache,
      supprimerTache,
      ajouterMembre,
      retirerMembre,
      ouvrirTache,
      getNomUtilisateur,
      getNomUtilisateurById,
      utilisateursDispo,
      ajouterUtilisateurTache,
      retirerUtilisateurTache,
      ajouterCommentaire,
      supprimerCommentaire,
      onDragStart,
      onDrop,
      onFichiersChange, 
      retirerFichier, 
      formatTaille, 
      telechargerFichier,
    };
  },
};
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
}
.back-link:hover {
  color: #1a1f36;
}
.page-header h1 {
  font-size: 1.5rem;
  font-weight: 700;
  color: #1a1f36;
}
.header-actions {
  display: flex;
  gap: 0.8rem;
  align-items: center;
}

.membres-panel {
  background: #fff;
  border-radius: 10px;
  padding: 1.5rem;
  margin-bottom: 1.5rem;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.06);
}
.membres-panel h3 {
  font-size: 1rem;
  font-weight: 700;
  color: #1a1f36;
  margin-bottom: 1rem;
}
.membres-liste {
  display: flex;
  flex-direction: column;
  gap: 0.6rem;
  margin-bottom: 1.5rem;
}
.membre-item {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 0.7rem 1rem;
  background: #f7fafc;
  border-radius: 8px;
}
.membre-info {
  display: flex;
  align-items: center;
  gap: 0.8rem;
}
.membre-avatar {
  background: #1a1f36;
  color: #fff;
  width: 32px;
  height: 32px;
  border-radius: 50%;
  display: flex;
  align-items: center;
  justify-content: center;
  font-size: 0.75rem;
  font-weight: 700;
}
.role-badge {
  font-size: 0.72rem;
  font-weight: 700;
  padding: 0.2rem 0.6rem;
  border-radius: 20px;
  text-transform: uppercase;
}
.role-badge.admin {
  background: #fef3c7;
  color: #d97706;
}
.role-badge.utilisateur {
  background: #e0f2fe;
  color: #0369a1;
}
.ajouter-membre h4 {
  font-size: 0.9rem;
  font-weight: 600;
  color: #4a5568;
  margin-bottom: 0.8rem;
}
.ajouter-membre-form {
  display: flex;
  gap: 0.8rem;
}
.ajouter-membre-form select {
  flex: 1;
  padding: 0.65rem 1rem;
  border: 1.5px solid #e2e8f0;
  border-radius: 8px;
  font-size: 0.9rem;
  outline: none;
  background: #fff;
}
.ajouter-membre-form select:focus {
  border-color: #4c6ef5;
}

.create-form {
  display: flex;
  gap: 0.8rem;
  margin-bottom: 1.5rem;
  background: #fff;
  padding: 1rem;
  border-radius: 10px;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.06);
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
  transition: background 0.2s;
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
  box-shadow: 0 1px 4px rgba(0, 0, 0, 0.08);
  cursor: grab;
  transition:
    box-shadow 0.2s,
    opacity 0.2s;
}
.tache-card:active {
  cursor: grabbing;
  opacity: 0.7;
}
.tache-card:hover {
  box-shadow: 0 4px 12px rgba(0, 0, 0, 0.12);
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
.priorite-badge.haute {
  background: #fff5f5;
  color: #e53e3e;
}
.priorite-badge.moyenne {
  background: #fffaf0;
  color: #d69e2e;
}
.priorite-badge.basse {
  background: #f0fff4;
  color: #38a169;
}
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
.tache-hint {
  font-size: 0.72rem;
  color: #cbd5e0;
  margin-top: 0.4rem;
  font-style: italic;
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
.btn-close:hover {
  color: #e53e3e;
}
.btn-add-tache-toggle {
  width: 100%;
  background: transparent;
  border: 1.5px dashed #cbd5e0;
  color: #718096;
  padding: 0.6rem;
  border-radius: 8px;
  font-size: 0.85rem;
  cursor: pointer;
  transition: all 0.2s;
  margin-top: 0.5rem;
}
.btn-add-tache-toggle:hover {
  background: #fff;
  color: #1a1f36;
  border-color: #a0aec0;
}
.tache-form {
  display: flex;
  flex-direction: column;
  gap: 0.5rem;
  background: #fff;
  border-radius: 8px;
  padding: 0.8rem;
  margin-top: 0.5rem;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.08);
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
.tache-form-actions {
  display: flex;
  gap: 0.5rem;
}

.modal-overlay {
  position: fixed;
  inset: 0;
  background: rgba(0, 0, 0, 0.5);
  display: flex;
  align-items: center;
  justify-content: center;
  z-index: 200;
}
.modal {
  background: #fff;
  border-radius: 12px;
  width: 100%;
  max-width: 520px;
  max-height: 85vh;
  overflow-y: auto;
  box-shadow: 0 20px 60px rgba(0, 0, 0, 0.3);
}
.modal-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 1.2rem 1.5rem;
  border-bottom: 1px solid #e2e8f0;
  background: #f7fafc;
  position: sticky;
  top: 0;
}
.modal-header h2 {
  font-size: 1.1rem;
  font-weight: 700;
  color: #1a1f36;
}
.btn-close-modal {
  background: transparent;
  border: none;
  font-size: 1rem;
  color: #a0aec0;
  cursor: pointer;
  transition: color 0.2s;
}
.btn-close-modal:hover {
  color: #e53e3e;
}
.modal-body {
  padding: 1.5rem;
  display: flex;
  flex-direction: column;
  gap: 1.2rem;
}
.modal-desc {
  color: #4a5568;
  font-size: 0.9rem;
  line-height: 1.5;
}
.modal-meta {
  display: flex;
  gap: 0.8rem;
  align-items: center;
}
.modal-section {
  border-top: 1px solid #f0f2f5;
  padding-top: 1rem;
}
.modal-section h4 {
  font-size: 0.85rem;
  font-weight: 700;
  color: #718096;
  text-transform: uppercase;
  letter-spacing: 0.5px;
  margin-bottom: 0.8rem;
}
.membres-assignes {
  display: flex;
  flex-wrap: wrap;
  gap: 0.5rem;
}
.membre-tag {
  display: flex;
  align-items: center;
  gap: 0.4rem;
  background: #edf2ff;
  color: #4c6ef5;
  padding: 0.3rem 0.7rem;
  border-radius: 20px;
  font-size: 0.82rem;
  font-weight: 600;
}
.membre-tag button {
  background: transparent;
  border: none;
  color: #4c6ef5;
  cursor: pointer;
  font-size: 0.75rem;
  padding: 0;
  transition: color 0.2s;
}
.membre-tag button:hover {
  color: #e53e3e;
}
.empty-msg {
  color: #a0aec0;
  font-size: 0.85rem;
  font-style: italic;
}

.commentaires-liste {
  display: flex;
  flex-direction: column;
  gap: 0.8rem;
  max-height: 250px;
  overflow-y: auto;
  margin-bottom: 1rem;
}
.commentaire-item {
  background: #f7fafc;
  border-radius: 8px;
  padding: 0.8rem;
}
.commentaire-header {
  display: flex;
  align-items: center;
  gap: 0.6rem;
  margin-bottom: 0.4rem;
}
.commentaire-auteur {
  font-size: 0.82rem;
  font-weight: 700;
  color: #1a1f36;
}
.commentaire-date {
  font-size: 0.75rem;
  color: #a0aec0;
  flex: 1;
}
.commentaire-contenu {
  font-size: 0.88rem;
  color: #4a5568;
  line-height: 1.4;
}
.commentaire-form {
  display: flex;
  gap: 0.6rem;
}
.commentaire-form input {
  flex: 1;
  padding: 0.65rem 1rem;
  border: 1.5px solid #e2e8f0;
  border-radius: 8px;
  font-size: 0.9rem;
  outline: none;
}
.commentaire-form input:focus {
  border-color: #4c6ef5;
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
.btn-primary:hover {
  background: #2d3561;
}
.btn-primary:disabled {
  background: #a0aec0;
  cursor: not-allowed;
}
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
.btn-secondary:hover {
  background: #f7fafc;
}
.btn-danger-sm {
  background: transparent;
  color: #e53e3e;
  border: 1.5px solid #fed7d7;
  padding: 0.35rem 0.8rem;
  border-radius: 6px;
  font-size: 0.8rem;
  cursor: pointer;
  transition: all 0.2s;
}
.btn-danger-sm:hover {
  background: #e53e3e;
  color: #fff;
  border-color: #e53e3e;
}

.pieces-jointes { display: flex; flex-wrap: wrap; gap: 0.4rem; margin-top: 0.5rem; }
.pj-item { display: flex; align-items: center; gap: 0.4rem; background: #f7fafc; border: 1px solid #e2e8f0; border-radius: 6px; padding: 0.3rem 0.6rem; font-size: 0.8rem; cursor: pointer; }
.pj-item:hover { background: #edf2f7; }
.pj-size { color: #a0aec0; font-size: 0.72rem; }
.commentaire-form-wrap { display: flex; flex-direction: column; gap: 0.4rem; }
.fichiers-preview { display: flex; flex-direction: column; gap: 0.3rem; }
.fichier-tag { display: flex; align-items: center; gap: 0.5rem; background: #edf2ff; border-radius: 6px; padding: 0.3rem 0.7rem; font-size: 0.8rem; }
.btn-attach { display: flex; align-items: center; justify-content: center; width: 38px; height: 38px; cursor: pointer; font-size: 1.1rem; border-radius: 8px; flex-shrink: 0; }
.btn-attach:hover { background: #f7fafc; }
</style>
