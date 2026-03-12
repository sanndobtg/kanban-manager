package fr.ubo.kanban.dtos.tableau;

import fr.ubo.kanban.dtos.utilisateur.UtilisateurResponseDto;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class TableauResponseDto {
    private Long id;
    private String nom;
    private List<UtilisateurResponseDto> utilisateurs;
}