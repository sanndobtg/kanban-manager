package fr.ubo.kanban.model;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

@Entity
@Table(name = "colonne")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Colonne {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(name = "nom", length = 45)
    private String nom;

    @ManyToOne
    @JoinColumn(name = "idTableau", nullable = false)
    private Tableau tableau;
}