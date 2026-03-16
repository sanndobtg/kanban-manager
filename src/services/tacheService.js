import api from "./api.js";

export const tacheService = {
  getAll() {
    return api.get("/taches");
  },
  getById(id) {
    return api.get(`/taches/${id}`);
  },
  getByColonne(idColonne) {
    return api.get(`/taches/colonne/${idColonne}`);
  },
  create(dto) {
    return api.post("/taches", dto);
  },
  update(id, dto) {
    return api.put(`/taches/${id}`, dto);
  },
  delete(id) {
    return api.delete(`/taches/${id}`);
  },
  ajouterUtilisateur(idTache, idUtilisateur) {
    return api.post(`/taches/${idTache}/utilisateurs/${idUtilisateur}`);
  },
  retirerUtilisateur(idTache, idUtilisateur) {
    return api.delete(`/taches/${idTache}/utilisateurs/${idUtilisateur}`);
  },
};
