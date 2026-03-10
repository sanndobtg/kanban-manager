package fr.ubo.kanban.repositories;

import fr.ubo.kanban.model.Colonne;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface ColonneRepository extends JpaRepository<Colonne, Integer> {
    List<Colonne> findByTableauId(Integer tableauId);
}