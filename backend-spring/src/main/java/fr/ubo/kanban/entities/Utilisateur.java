package fr.ubo.kanban.entities;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

@Entity
@Table(name = "utilisateur")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Utilisateur {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(name = "nom", nullable = false, length = 45)
    private String nom;

    @Column(name = "prenom", nullable = false, length = 45)
    private String prenom;

    @Column(name = "email", nullable = false, length = 80)
    private String email;

    @Column(name = "motDePasse", nullable = false, length = 64)
    private String motDePasse;

    @Column(name = "role", nullable = false, length = 45)
    @Enumerated(EnumType.STRING)
    private Role role;
}