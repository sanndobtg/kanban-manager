package fr.ubo.kanban.entities;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

@Entity
@Table(name = "tache")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Tache {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(name = "titre", nullable = false, length = 45)
    private String titre;

    @Column(name = "description", nullable = false, length = 45)
    private String description;

    @ManyToOne
    @JoinColumn(name = "idColonne", nullable = false)
    private Colonne colonne;

    @ManyToOne
    @JoinColumn(name = "idUtilisateur", nullable = false)
    private Utilisateur utilisateur;

    @Column(name = "priorite", nullable = false, length = 45)
    @Enumerated(EnumType.STRING)
    private Priorite priorite;
}