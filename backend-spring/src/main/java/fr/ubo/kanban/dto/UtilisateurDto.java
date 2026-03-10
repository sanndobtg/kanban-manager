package fr.ubo.kanban.dto;

import fr.ubo.kanban.model.Utilisateur;
import jakarta.persistence.Column;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class UtilisateurDto {


    private Long id;

    private String nom;

    private String prenom;

    private String email;

    private String motDePasse;

    private Utilisateur.Role role;

    public Long getId() {
        return id;
    }

    public String getNom() {
        return nom;
    }

    public String getPrenom() {
        return prenom;
    }

    public String getEmail() {
        return email;
    }

    public String getMotDePasse() {
        return motDePasse;
    }

    public Utilisateur.Role getRole() {
        return role;
    }
}