package fr.ubo.kanban.dtos.tache;

import fr.ubo.kanban.model.Priorite;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class TacheDto {
    private Long id;
    private String titre;
    private String description;
    private Long idColonne;
    private Long idUtilisateur;
    private Priorite priorite;
}