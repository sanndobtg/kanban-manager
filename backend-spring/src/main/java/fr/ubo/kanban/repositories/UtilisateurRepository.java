package fr.ubo.kanban.repositories;

import fr.ubo.kanban.model.Utilisateur;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UtilisateurRepository extends JpaRepository<Utilisateur,Long> {


}
