package fr.ubo.kanban.services;

import fr.ubo.kanban.entities.Utilisateur;
import java.util.List;

public interface UtilisateurService {
    List<Utilisateur> findAll();
    Utilisateur findById(Integer id);
    Utilisateur findByEmail(String email);
    Utilisateur save(Utilisateur utilisateur);
    void delete(Integer id);
}