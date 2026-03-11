package fr.ubo.kanban.mappers;

import fr.ubo.kanban.dtos.tableau.TableauDto;
import fr.ubo.kanban.model.Tableau;
import org.springframework.stereotype.Component;

@Component
public class TableauMapper {

    public TableauDto toDto(Tableau tableau) {
        TableauDto dto = new TableauDto();
        dto.setId(tableau.getId());
        dto.setNom(tableau.getNom());
        dto.setUtilisateurs(tableau.getUtilisateurs());
        return dto;
    }

    public Tableau toEntity(TableauDto dto) {
        Tableau tableau = new Tableau();
        tableau.setNom(dto.getNom());
        tableau.setUtilisateurs(dto.getUtilisateurs());
        return tableau;
    }
}