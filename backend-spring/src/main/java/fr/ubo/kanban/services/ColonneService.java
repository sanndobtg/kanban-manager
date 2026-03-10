package fr.ubo.kanban.services;

import fr.ubo.kanban.model.Colonne;
import java.util.List;

public interface ColonneService {
    List<Colonne> findByTableauId(Integer tableauId);
    Colonne findById(Integer id);
    Colonne save(Colonne colonne);
    void delete(Integer id);
}