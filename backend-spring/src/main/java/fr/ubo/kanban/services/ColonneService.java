package fr.ubo.kanban.services;

import fr.ubo.kanban.dtos.colonne.ColonneDto;

import java.util.List;

public interface ColonneService {
    ColonneDto saveColonne(ColonneDto dto);
    ColonneDto getColonneById(Long id);
    List<ColonneDto> getAllColonnes();
    List<ColonneDto> getColonnesByTableauId(Long idTableau);
    ColonneDto updateColonne(Long id, ColonneDto dto);
    boolean deleteColonne(Long id);
}