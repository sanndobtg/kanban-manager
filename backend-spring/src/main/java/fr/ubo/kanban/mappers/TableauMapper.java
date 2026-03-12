package fr.ubo.kanban.mappers;

import fr.ubo.kanban.dtos.tableau.TableauRequestDto;
import fr.ubo.kanban.dtos.tableau.TableauResponseDto;
import fr.ubo.kanban.dtos.utilisateur.UtilisateurResponseDto;
import fr.ubo.kanban.model.Tableau;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

@Component
public class TableauMapper {

    public Tableau toEntity(TableauRequestDto dto) {
        Tableau tableau = new Tableau();
        tableau.setNom(dto.getNom());
        return tableau;
    }

    public TableauResponseDto toResponseDto(Tableau tableau) {
        TableauResponseDto dto = new TableauResponseDto();
        dto.setId(tableau.getId());
        dto.setNom(tableau.getNom());

        List<UtilisateurResponseDto> utilisateurs = new ArrayList<>();
        if (tableau.getUtilisateurs() != null) {
            for (var u : tableau.getUtilisateurs()) {
                UtilisateurResponseDto uDto = new UtilisateurResponseDto();
                uDto.setId(u.getId());
                uDto.setNom(u.getNom());
                uDto.setPrenom(u.getPrenom());
                uDto.setEmail(u.getEmail());
                uDto.setRole(u.getRole());
                utilisateurs.add(uDto);
            }
        }
        dto.setUtilisateurs(utilisateurs);
        return dto;
    }
}