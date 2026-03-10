package fr.ubo.kanban.model;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import java.io.Serializable;

@Embeddable
@Data
@NoArgsConstructor
@AllArgsConstructor
public class TableauAssocieId implements Serializable {

    @Column(name = "Tableau_id")
    private Integer tableauId;

    @Column(name = "Utilisateur_id")
    private Integer utilisateurId;
}