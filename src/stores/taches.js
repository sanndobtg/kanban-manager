import { defineStore } from 'pinia'
import { tacheService } from '../services/tacheService.js'

export const useTachesStore = defineStore('taches', {
  state: () => ({
    taches: [],
  }),

  actions: {
    async fetchByColonne(idColonne) {
      const res = await tacheService.getByColonne(idColonne)
      this.taches = res.data
    },

    async create(dto) {
      const res = await tacheService.create(dto)
      this.taches.push(res.data)
    },

    async update(id, dto) {
      const res = await tacheService.update(id, dto)
      const index = this.taches.findIndex(t => t.id === id)
      this.taches[index] = res.data
    },

    async delete(id) {
      await tacheService.delete(id)
      this.taches = this.taches.filter(t => t.id !== id)
    }
  }
})