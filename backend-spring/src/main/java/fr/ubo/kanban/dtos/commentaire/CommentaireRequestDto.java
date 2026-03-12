package fr.ubo.kanban.dtos.commentaire;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CommentaireRequestDto {
    private Long idUtilisateur;
    private String contenu;
}