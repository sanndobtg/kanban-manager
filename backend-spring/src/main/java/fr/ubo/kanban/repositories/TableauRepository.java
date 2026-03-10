package fr.ubo.kanban.repositories;

import fr.ubo.kanban.entities.Tableau;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface TableauRepository extends JpaRepository<Tableau, Integer> {

}