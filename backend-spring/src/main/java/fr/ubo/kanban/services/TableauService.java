package fr.ubo.kanban.services;

import fr.ubo.kanban.dtos.tableau.TableauRequestDto;
import fr.ubo.kanban.dtos.tableau.TableauResponseDto;

import java.util.List;

public interface TableauService {
    List<TableauResponseDto> findAll();
    TableauResponseDto findById(Long id);
    TableauResponseDto create(TableauRequestDto dto);
    TableauResponseDto update(Long id, TableauRequestDto dto);
    void delete(Long id);
    void ajouterMembre(Long idTableau, Long idUtilisateur);
    void retirerMembre(Long idTableau, Long idUtilisateur);
}