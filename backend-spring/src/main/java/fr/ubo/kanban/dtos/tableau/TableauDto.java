package fr.ubo.kanban.dtos.tableau;

import fr.ubo.kanban.model.Utilisateur;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class TableauDto {
    private Long id;
    private String nom;
    private List<Utilisateur> utilisateurs = new ArrayList<>();
}