package fr.ubo.kanban.services;

import fr.ubo.kanban.dtos.piecejointe.PieceJointeResponseDto;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

public interface FichierService {
    PieceJointeResponseDto upload(MultipartFile file, Long idUtilisateur);
    PieceJointeResponseDto getById(String id);
    List<PieceJointeResponseDto> getByIds(List<String> ids);
    byte[] getContenu(String id);
    String getContentType(String id);
    String getNomFichier(String id);
    void delete(String id);
}