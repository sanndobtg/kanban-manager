package fr.ubo.kanban.repositories;

import fr.ubo.kanban.model.mongoDB.PieceJointe;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.List;

public interface PieceJointeRepository extends MongoRepository<PieceJointe, String> {
    List<PieceJointe> findByIdIn(List<String> ids);
}