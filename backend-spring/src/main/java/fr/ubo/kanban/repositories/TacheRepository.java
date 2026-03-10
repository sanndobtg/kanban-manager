package fr.ubo.kanban.repositories;

import fr.ubo.kanban.entities.Tache;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface TacheRepository extends JpaRepository<Tache, Integer> {
    List<Tache> findByColonneId(Integer colonneId);
}