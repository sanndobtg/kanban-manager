package fr.ubo.kanban.services.impl;

import fr.ubo.kanban.common.exception.NotFoundException;
import fr.ubo.kanban.dtos.piecejointe.PieceJointeResponseDto;
import fr.ubo.kanban.model.mongoDB.PieceJointe;
import fr.ubo.kanban.repositories.PieceJointeRepository;
import fr.ubo.kanban.services.FichierService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;

@Service
@RequiredArgsConstructor
public class FichierServiceImpl implements FichierService {

    private final PieceJointeRepository pieceJointeRepository;

    private static final long MAX_FILE_SIZE = 10 * 1024 * 1024; // 10 Mo

    @Override
    public PieceJointeResponseDto upload(MultipartFile file, Long idUtilisateur) {
        if (file.isEmpty()) {
            throw new IllegalArgumentException("Le fichier est vide");
        }
        if (file.getSize() > MAX_FILE_SIZE) {
            throw new IllegalArgumentException("Le fichier dépasse 10 Mo");
        }

        try {
            PieceJointe pj = new PieceJointe();
            pj.setNomFichier(file.getOriginalFilename());
            pj.setContentType(file.getContentType());
            pj.setTaille(file.getSize());
            pj.setContenu(file.getBytes());
            pj.setIdUtilisateur(idUtilisateur);
            pj.setDateUpload(LocalDateTime.now());

            pj = pieceJointeRepository.save(pj);
            return toDto(pj);
        } catch (IOException e) {
            throw new RuntimeException("Erreur lors de l'upload", e);
        }
    }

    @Override
    public PieceJointeResponseDto getById(String id) {
        return toDto(findOrThrow(id));
    }

    @Override
    public List<PieceJointeResponseDto> getByIds(List<String> ids) {
        if (ids == null || ids.isEmpty()) return Collections.emptyList();
        return pieceJointeRepository.findByIdIn(ids).stream().map(this::toDto).toList();
    }

    @Override
    public byte[] getContenu(String id) {
        return findOrThrow(id).getContenu();
    }

    @Override
    public String getContentType(String id) {
        return findOrThrow(id).getContentType();
    }

    @Override
    public String getNomFichier(String id) {
        return findOrThrow(id).getNomFichier();
    }

    @Override
    public void delete(String id) {
        findOrThrow(id);
        pieceJointeRepository.deleteById(id);
    }

    private PieceJointe findOrThrow(String id) {
        return pieceJointeRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Pièce jointe non trouvée : " + id));
    }

    private PieceJointeResponseDto toDto(PieceJointe pj) {
        PieceJointeResponseDto dto = new PieceJointeResponseDto();
        dto.setId(pj.getId());
        dto.setNomFichier(pj.getNomFichier());
        dto.setContentType(pj.getContentType());
        dto.setTaille(pj.getTaille());
        dto.setIdUtilisateur(pj.getIdUtilisateur());
        dto.setDateUpload(pj.getDateUpload());
        dto.setUrlTelechargement("/fichiers/" + pj.getId() + "/download");
        return dto;
    }
}