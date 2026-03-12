package fr.ubo.kanban.dtos.commentaire;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class CommentaireDto {
    private String id;
    private Long idTache;
    private Long idUtilisateur;
    private String contenu;
    private LocalDateTime dateCreation;
}