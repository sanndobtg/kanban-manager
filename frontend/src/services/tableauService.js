import api from './api.js'

export const tableauService = {
  getAll()                        { return api.get('/tableaux') },
  getById(id)                     { return api.get(`/tableaux/${id}`) },
  create(dto)                     { return api.post('/tableaux', dto) },
  update(id, dto)                 { return api.put(`/tableaux/${id}`, dto) },
  delete(id)                      { return api.delete(`/tableaux/${id}`) },
  ajouterMembre(idTableau, idUtilisateur) {
    return api.post(`/tableaux/${idTableau}/membres/${idUtilisateur}`)
  },
  retirerMembre(idTableau, idUtilisateur) {
    return api.delete(`/tableaux/${idTableau}/membres/${idUtilisateur}`)
  },
}