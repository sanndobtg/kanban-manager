package fr.ubo.kanban.repositories;

import fr.ubo.kanban.model.mongoDB.Commentaire;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.List;

public interface CommentaireRepository extends MongoRepository<Commentaire, String> {
    List<Commentaire> findByIdTache(Long idTache);
    void deleteAllByIdTache(Long idTache);
}