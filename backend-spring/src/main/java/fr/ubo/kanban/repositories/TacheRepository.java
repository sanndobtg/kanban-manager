package fr.ubo.kanban.repositories;

import fr.ubo.kanban.model.Tache;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface TacheRepository extends JpaRepository<Tache, Long> {
    List<Tache> findByColonneId(Long colonneId);
}