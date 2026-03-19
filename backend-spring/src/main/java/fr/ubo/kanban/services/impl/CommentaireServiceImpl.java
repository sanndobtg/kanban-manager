package fr.ubo.kanban.services.impl;

import fr.ubo.kanban.common.exception.NotFoundException;
import fr.ubo.kanban.dtos.commentaire.CommentaireRequestDto;
import fr.ubo.kanban.dtos.commentaire.CommentaireResponseDto;
import fr.ubo.kanban.mappers.CommentaireMapper;
import fr.ubo.kanban.model.mongoDB.Commentaire;
import fr.ubo.kanban.model.Tache;
import fr.ubo.kanban.repositories.CommentaireRepository;
import fr.ubo.kanban.repositories.TacheRepository;
import fr.ubo.kanban.services.CommentaireService;
import fr.ubo.kanban.services.FichierService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.server.ResponseStatusException;

import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)  // ← Sanndo
public class CommentaireServiceImpl implements CommentaireService {

    private final CommentaireRepository commentaireRepository;
    private final CommentaireMapper commentaireMapper;
    private final TacheRepository tacheRepository;
    private final FichierService fichierService;

    @Override
    public List<CommentaireResponseDto> findByIdTache(Long idTache) {
        // Vérifie que l'utilisateur connecté est assigné à la tâche
        verifierAccesTache(idTache);

        return commentaireRepository.findByIdTache(idTache)
                .stream()
                .map(this::toResponseWithFiles)
                .toList();
    }

    @Override
    @Transactional
    public CommentaireResponseDto create(Long idTache, CommentaireRequestDto dto) {
        // Vérifie que l'utilisateur connecté est assigné à la tâche
        verifierAccesTache(idTache);

        // Récupère l'id depuis le JWT — ignore dto.idUtilisateur
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        Long idUtilisateur = Long.parseLong(auth.getName());

        Commentaire commentaire = commentaireMapper.toEntity(dto, idTache, idUtilisateur);
        return commentaireMapper.toResponseDto(commentaireRepository.save(commentaire));
    }

    @Override
    public void delete(String id) {
        Commentaire commentaire = commentaireRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Commentaire non trouvé : " + id));

        // Vérifie que c'est bien l'auteur qui supprime
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        Long idConnecte = Long.parseLong(auth.getName());

        if (!commentaire.getIdUtilisateur().equals(idConnecte)) {
            throw new ResponseStatusException(
                    HttpStatus.FORBIDDEN,
                    "Seul l'auteur peut supprimer ce commentaire"
            );
        }
        if (commentaire.getPiecesJointesIds() != null) {
            commentaire.getPiecesJointesIds().forEach(pjId -> {
                try { fichierService.delete(pjId); } catch (Exception ignored) {}
            });
        }
        commentaireRepository.deleteById(id);
    }

    // Vérifie que l'utilisateur connecté est assigné à la tâche
    private void verifierAccesTache(Long idTache) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        Long idConnecte = Long.parseLong(auth.getName());

        Tache tache = tacheRepository.findById(idTache)
                .orElseThrow(() -> new NotFoundException("Tache non trouvée : " + idTache));

        boolean estAssigne = tache.getUtilisateurs().stream()
                .anyMatch(u -> u.getId().equals(idConnecte));
        boolean estCreateur = tache.getCreateur().getId().equals(idConnecte);

        if (!estAssigne && !estCreateur) {
            throw new ResponseStatusException(
                    HttpStatus.FORBIDDEN,
                    "Accès refusé : vous n'êtes pas assigné à cette tâche"
            );
        }
    }

    @Override
    @Transactional
    public CommentaireResponseDto createWithFiles(Long idTache, CommentaireRequestDto dto,
                                                  List<MultipartFile> fichiers) {
        verifierAccesTache(idTache);

        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        Long idUtilisateur = Long.parseLong(auth.getName());

        Commentaire commentaire = commentaireMapper.toEntity(dto, idTache, idUtilisateur);

        List<String> pjIds = new ArrayList<>();
        if (fichiers != null) {
            for (MultipartFile fichier : fichiers) {
                if (!fichier.isEmpty()) {
                    pjIds.add(fichierService.upload(fichier, idUtilisateur).getId());
                }
            }
        }
        commentaire.setPiecesJointesIds(pjIds);
        commentaire = commentaireRepository.save(commentaire);

        return toResponseWithFiles(commentaire);
    }

    private CommentaireResponseDto toResponseWithFiles(Commentaire commentaire) {
        CommentaireResponseDto dto = commentaireMapper.toResponseDto(commentaire);
        if (commentaire.getPiecesJointesIds() != null && !commentaire.getPiecesJointesIds().isEmpty()) {
            dto.setPiecesJointes(fichierService.getByIds(commentaire.getPiecesJointesIds()));
        }
        return dto;
    }
}