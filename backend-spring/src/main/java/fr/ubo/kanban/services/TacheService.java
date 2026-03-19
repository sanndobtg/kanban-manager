package fr.ubo.kanban.services;

import fr.ubo.kanban.dtos.tache.TacheDto;

import java.util.List;

public interface TacheService {
    List<TacheDto> getAllTaches();
    TacheDto getTacheById(Long id);
    List<TacheDto> getTachesByColonneId(Long idColonne);
    TacheDto saveTache(TacheDto dto);
    TacheDto updateTache(Long id, TacheDto dto);
    void deleteTache(Long id);
    void ajouterUtilisateur(Long idTache, Long idUtilisateur);
    void retirerUtilisateur(Long idTache, Long idUtilisateur);
}