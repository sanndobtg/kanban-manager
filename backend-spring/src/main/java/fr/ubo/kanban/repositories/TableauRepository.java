package fr.ubo.kanban.repositories;

import fr.ubo.kanban.model.Tableau;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface TableauRepository extends JpaRepository<Tableau, Long> {

}