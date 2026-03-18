package fr.ubo.kanban.dtos.commentaire;

import fr.ubo.kanban.dtos.piecejointe.PieceJointeResponseDto;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CommentaireResponseDto {
    private String id;
    private Long idTache;
    private Long idUtilisateur;
    private String contenu;
    private LocalDateTime dateCreation;
    private List<PieceJointeResponseDto> piecesJointes = new ArrayList<>();
}