import api from './api.js'

export const colonneService = {
  getAll()                { return api.get('/colonnes') },
  getById(id)             { return api.get(`/colonnes/${id}`) },
  getByTableau(idTableau) { return api.get(`/colonnes/tableau/${idTableau}`) },
  create(dto)             { return api.post('/colonnes', dto) },
  update(id, dto)         { return api.put(`/colonnes/${id}`, dto) },
  delete(id)              { return api.delete(`/colonnes/${id}`) },
}