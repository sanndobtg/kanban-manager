package fr.ubo.kanban.mappers;

import fr.ubo.kanban.dtos.commentaire.CommentaireRequestDto;
import fr.ubo.kanban.dtos.commentaire.CommentaireResponseDto;
import fr.ubo.kanban.model.mongoDB.Commentaire;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.ArrayList;

@Component
public class CommentaireMapper {

    public CommentaireResponseDto toResponseDto(Commentaire commentaire) {
        CommentaireResponseDto dto = new CommentaireResponseDto();
        dto.setId(commentaire.getId());
        dto.setIdTache(commentaire.getIdTache());
        dto.setIdUtilisateur(commentaire.getIdUtilisateur());
        dto.setContenu(commentaire.getContenu());
        dto.setDateCreation(commentaire.getDateCreation());
        dto.setPiecesJointes(new ArrayList<>());
        return dto;
    }

    // idUtilisateur vient du JWT, pas du dto
    public Commentaire toEntity(CommentaireRequestDto dto, Long idTache, Long idUtilisateur) {
        Commentaire commentaire = new Commentaire();
        commentaire.setIdTache(idTache);
        commentaire.setIdUtilisateur(idUtilisateur);
        commentaire.setContenu(dto.getContenu());
        commentaire.setDateCreation(LocalDateTime.now());
        commentaire.setPiecesJointesIds(new ArrayList<>());
        return commentaire;
    }
}