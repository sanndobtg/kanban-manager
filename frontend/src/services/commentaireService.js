import api from './api.js'

export const commentaireService = {
  getByTache(idTache) {
    return api.get(`/taches/${idTache}/commentaires`)
  },

  create(idTache, contenu, fichiers = []) {
    if (fichiers.length > 0) {
      const formData = new FormData()
      formData.append('contenu', contenu)
      for (const f of fichiers) formData.append('fichiers', f)
      return api.post(`/taches/${idTache}/commentaires`, formData, {
        headers: { 'Content-Type': 'multipart/form-data' },
      })
    }
    return api.post(`/taches/${idTache}/commentaires`, { contenu })
  },

  delete(id) {
    return api.delete(`/commentaires/${id}`)
  },
}