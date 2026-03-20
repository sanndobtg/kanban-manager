import api from './api.js'

export const statsService = {
  getStats() {
    return api.get('/admin/stats')
  },
}