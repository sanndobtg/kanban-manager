package fr.ubo.kanban.model;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

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
    private Integer id;

    @Column(name = "nom", nullable = false, length = 45)
    private String nom;

    @ManyToMany
    @JoinTable(
            name = "tableauassocie",
            joinColumns = @JoinColumn(name = "Tableau_id"),
            inverseJoinColumns = @JoinColumn(name = "Utilisateur_id")
    )
    private List<Utilisateur> utilisateurs = new ArrayList<>();
}