package fr.ubo.kanban.dtos.utilisateur;

import fr.ubo.kanban.model.Role;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class UtilisateurRequestDto {
    private String nom;
    private String prenom;
    private String email;
    private String motDePasse;
    private Role role;
}