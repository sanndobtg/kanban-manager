package fr.ubo.kanban.dtos.tache;

import com.fasterxml.jackson.annotation.JsonProperty;
import fr.ubo.kanban.model.Priorite;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class TacheDto {

    @JsonProperty(access = JsonProperty.Access.READ_ONLY)
    private Long id;

    private String titre;
    private String description;
    private Long idColonne;

    // Créateur — READ_ONLY, rempli automatiquement par le service
    @JsonProperty(access = JsonProperty.Access.READ_ONLY)
    private Long idCreateur;

    // Liste des ids des utilisateurs assignés
    private List<Long> idUtilisateurs = new ArrayList<>();

    private Priorite priorite;
    private LocalDate dateLimit;
}