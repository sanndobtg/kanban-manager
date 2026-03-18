package fr.ubo.kanban.services;

import fr.ubo.kanban.dtos.commentaire.CommentaireRequestDto;
import fr.ubo.kanban.dtos.commentaire.CommentaireResponseDto;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

public interface CommentaireService {
    List<CommentaireResponseDto> findByIdTache(Long idTache);
    CommentaireResponseDto create(Long idTache, CommentaireRequestDto dto);
    void delete(String id);
    CommentaireResponseDto createWithFiles(Long idTache, CommentaireRequestDto dto,
                                           List<MultipartFile> fichiers);
}