package fr.ubo.kanban.dtos.tache;

import com.fasterxml.jackson.annotation.JsonProperty;
import fr.ubo.kanban.model.Priorite;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class TacheDto {
    @JsonProperty(access = JsonProperty.Access.READ_ONLY)
    private Long id;

    private String titre;
    private String description;
    private Long idColonne;
    private Long idUtilisateur;
    private Priorite priorite;
    private LocalDate dateLimit;
}