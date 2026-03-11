package fr.ubo.kanban.services;

import fr.ubo.kanban.dtos.tableau.TableauDto;

import java.util.List;

public interface TableauService {
    TableauDto saveTableau(TableauDto dto);
    TableauDto getTableauById(Long id);
    List<TableauDto> getAllTableaux();
    TableauDto updateTableau(Long id, TableauDto dto);
    boolean deleteTableau(Long id);
}