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
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

@Service
@RequiredArgsConstructor
public class CommentaireServiceImpl implements CommentaireService {

    private final CommentaireRepository commentaireRepository;
    private final CommentaireMapper commentaireMapper;
    private final TacheRepository tacheRepository;

    @Override
    public List<CommentaireResponseDto> findByIdTache(Long idTache) {
        // Vérifie que l'utilisateur connecté est assigné à la tâche
        verifierAccesTache(idTache);

        return commentaireRepository.findByIdTache(idTache)
                .stream()
                .map(commentaireMapper::toResponseDto)
                .toList();
    }

    @Override
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
}