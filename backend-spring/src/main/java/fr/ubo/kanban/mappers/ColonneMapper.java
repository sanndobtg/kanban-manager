package fr.ubo.kanban.mappers;

import fr.ubo.kanban.dtos.colonne.ColonneDto;
import fr.ubo.kanban.model.Colonne;
import fr.ubo.kanban.model.Tableau;
import org.springframework.stereotype.Component;

@Component
public class ColonneMapper {

    public ColonneDto toDto(Colonne colonne) {
        ColonneDto dto = new ColonneDto();
        dto.setId(colonne.getId());
        dto.setNom(colonne.getNom());
        dto.setIdTableau(colonne.getTableau().getId());
        return dto;
    }

    public Colonne toEntity(ColonneDto dto, Tableau tableau) {
        Colonne colonne = new Colonne();
        colonne.setNom(dto.getNom());
        colonne.setTableau(tableau);
        return colonne;
    }
}