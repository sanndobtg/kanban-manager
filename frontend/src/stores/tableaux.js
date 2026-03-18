import { defineStore } from 'pinia'
import { tableauService } from '../services/tableauService.js'

export const useTableauxStore = defineStore('tableaux', {
  state: () => ({
    tableaux: [],
    tableauActuel: null,
  }),

  actions: {
    async fetchAll() {
      const res = await tableauService.getAll()
      this.tableaux = res.data.data
    },

    async fetchById(id) {
      const res = await tableauService.getById(id)
      this.tableauActuel = res.data.data
    },

    async create(dto) {
      const res = await tableauService.create(dto)
      this.tableaux.push(res.data.data)
    },

    async update(id, dto) {
      const res = await tableauService.update(id, dto)
      const index = this.tableaux.findIndex(t => t.id === id)
      this.tableaux[index] = res.data.data
    },

    async delete(id) {
      await tableauService.delete(id)
      this.tableaux = this.tableaux.filter(t => t.id !== id)
    }
  }
})