package fr.ubo.kanban.services;

import fr.ubo.kanban.model.Tache;
import java.util.List;

public interface TacheService {
    List<Tache> findByColonneId(Integer colonneId);
    Tache findById(Integer id);
    Tache save(Tache tache);
    void delete(Integer id);
}