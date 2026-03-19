package fr.ubo.kanban.dtos.piecejointe;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class PieceJointeResponseDto {
    private String id;
    private String nomFichier;
    private String contentType;
    private long taille;
    private Long idUtilisateur;
    private LocalDateTime dateUpload;
    private String urlTelechargement;
}