package fr.ubo.kanban.common.sql;

import fr.ubo.kanban.model.Utilisateur;
import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

@Entity
@Table(name = "tableauassocie")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class TableauAssocie {

    @EmbeddedId
    private TableauAssocieId id;

    @ManyToOne
    @MapsId("tableauId")
    @JoinColumn(name = "Tableau_id")
    private Tableau tableau;

    @ManyToOne
    @MapsId("utilisateurId")
    @JoinColumn(name = "Utilisateur_id")
    private Utilisateur utilisateur;
}
