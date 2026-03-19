package fr.ubo.kanban.model;

import fr.ubo.kanban.model.Colonne;
import fr.ubo.kanban.model.Priorite;
import fr.ubo.kanban.model.Utilisateur;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "tache")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Tache {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "titre", nullable = false, length = 45)
    private String titre;

    @Column(name = "description", nullable = false, length = 45)
    private String description;

    @ManyToOne
    @JoinColumn(name = "idColonne", nullable = false)
    private Colonne colonne;

    // Créateur de la tâche — seul lui peut supprimer
    @ManyToOne
    @JoinColumn(name = "idCreateur", nullable = false)
    private Utilisateur createur;

    // Membres assignés à la tâche
    @ManyToMany
    @JoinTable(
            name = "tacheassociee",
            joinColumns = @JoinColumn(name = "Tache_id"),
            inverseJoinColumns = @JoinColumn(name = "Utilisateur_id")
    )
    private List<Utilisateur> utilisateurs = new ArrayList<>();

    @Column(name = "priorite", nullable = false, length = 45)
    @Enumerated(EnumType.STRING)
    private Priorite priorite;

    @Column(name = "dateLimit")
    private LocalDate dateLimit;
}