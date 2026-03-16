import api from './api.js'

export const commentaireService = {
  getByTache(idTache)       { return api.get(`/taches/${idTache}/commentaires`) },
  create(idTache, dto)      { return api.post(`/taches/${idTache}/commentaires`, dto) },
  delete(id)                { return api.delete(`/commentaires/${id}`) },
}