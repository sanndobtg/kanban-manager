package fr.ubo.kanban.services;

import fr.ubo.kanban.dtos.tache.TacheDto;

import java.util.List;

public interface TacheService {
    TacheDto saveTache(TacheDto dto);
    TacheDto getTacheById(Long id);
    List<TacheDto> getAllTaches();
    List<TacheDto> getTachesByColonneId(Long idColonne);
    TacheDto updateTache(Long id, TacheDto dto);
    boolean deleteTache(Long id);
}