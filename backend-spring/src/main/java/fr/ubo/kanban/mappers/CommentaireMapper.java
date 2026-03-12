package fr.ubo.kanban.mappers;

import fr.ubo.kanban.dtos.commentaire.CommentaireRequestDto;
import fr.ubo.kanban.dtos.commentaire.CommentaireResponseDto;
import fr.ubo.kanban.model.mongoDB.Commentaire;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

@Component
public class CommentaireMapper {

    public Commentaire toEntity(CommentaireRequestDto dto, Long idTache) {
        Commentaire commentaire = new Commentaire();
        commentaire.setIdTache(idTache);
        commentaire.setIdUtilisateur(dto.getIdUtilisateur());
        commentaire.setContenu(dto.getContenu());
        commentaire.setDateCreation(LocalDateTime.now());
        return commentaire;
    }

    public CommentaireResponseDto toResponseDto(Commentaire commentaire) {
        CommentaireResponseDto dto = new CommentaireResponseDto();
        dto.setId(commentaire.getId());
        dto.setIdTache(commentaire.getIdTache());
        dto.setIdUtilisateur(commentaire.getIdUtilisateur());
        dto.setContenu(commentaire.getContenu());
        dto.setDateCreation(commentaire.getDateCreation());
        return dto;
    }
}