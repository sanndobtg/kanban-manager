package fr.ubo.kanban.dtos.commentaire;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CommentaireResponseDto {
    private String id;
    private Long idTache;
    private Long idUtilisateur;
    private String contenu;
    private LocalDateTime dateCreation;
}