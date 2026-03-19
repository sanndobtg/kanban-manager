import api from './api.js'

export const utilisateurService = {
  getAll()          { return api.get('/utilisateurs') },
  getById(id)       { return api.get(`/utilisateurs/${id}`) },
  create(dto)       { return api.post('/utilisateurs', dto) },
  update(id, dto)   { return api.put(`/utilisateurs/${id}`, dto) },
  delete(id)        { return api.delete(`/utilisateurs/${id}`) },
}