import { defineStore } from "pinia";
import { tacheService } from "../services/tacheService.js";

export const useTachesStore = defineStore("taches", {
  state: () => ({
    // Stocke les tâches par colonne : { idColonne: [taches] }
    tachesParColonne: {},
  }),

  actions: {
    async fetchByColonne(idColonne) {
      const res = await tacheService.getByColonne(idColonne);
      // Stocke par colonne séparément — ne touche pas aux autres colonnes
      this.tachesParColonne[idColonne] = res.data;
    },

    async create(dto) {
      const res = await tacheService.create(dto);
      const tache = res.data;
      // Ajoute uniquement dans la bonne colonne
      if (!this.tachesParColonne[tache.idColonne]) {
        this.tachesParColonne[tache.idColonne] = [];
      }
      this.tachesParColonne[tache.idColonne].push(tache);
    },

    async update(id, dto) {
      const res = await tacheService.update(id, dto);
      const tache = res.data;
      const liste = this.tachesParColonne[tache.idColonne] || [];
      const index = liste.findIndex((t) => t.id === id);
      if (index !== -1) liste[index] = tache;
    },

    async delete(id, idColonne) {
      await tacheService.delete(id);
      if (this.tachesParColonne[idColonne]) {
        this.tachesParColonne[idColonne] = this.tachesParColonne[
          idColonne
        ].filter((t) => t.id !== id);
      }
    },

    // Pour le drag & drop — déplace une tâche d'une colonne à une autre
    async deplacerTache(tache, idColonneSource, idColonneCible) {
      const tacheMaj = { ...tache, idColonne: idColonneCible };
      await tacheService.update(tache.id, tacheMaj);
      // Retire de la colonne source
      this.tachesParColonne[idColonneSource] = this.tachesParColonne[
        idColonneSource
      ].filter((t) => t.id !== tache.id);
      // Ajoute dans la colonne cible
      if (!this.tachesParColonne[idColonneCible]) {
        this.tachesParColonne[idColonneCible] = [];
      }
      this.tachesParColonne[idColonneCible].push(tacheMaj);
    },
  },
});
