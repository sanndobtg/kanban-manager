package fr.ubo.kanban.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "tableau")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Tableau {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "nom", nullable = false, length = 45)
    private String nom;

    // Créateur du tableau
    @ManyToOne
    @JoinColumn(name = "idCreateur", nullable = true)
    private Utilisateur createur;

    @ManyToMany
    @JoinTable(
            name = "tableauassocie",
            joinColumns = @JoinColumn(name = "Tableau_id"),
            inverseJoinColumns = @JoinColumn(name = "Utilisateur_id")
    )
    private List<Utilisateur> utilisateurs = new ArrayList<>();
}