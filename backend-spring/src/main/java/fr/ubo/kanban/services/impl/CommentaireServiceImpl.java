package fr.ubo.kanban.services.impl;

import fr.ubo.kanban.common.exception.NotFoundException;
import fr.ubo.kanban.dtos.commentaire.CommentaireRequestDto;
import fr.ubo.kanban.dtos.commentaire.CommentaireResponseDto;
import fr.ubo.kanban.mappers.CommentaireMapper;
import fr.ubo.kanban.model.mongoDB.Commentaire;
import fr.ubo.kanban.repositories.CommentaireRepository;
import fr.ubo.kanban.services.CommentaireService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class CommentaireServiceImpl implements CommentaireService {

    private final CommentaireRepository commentaireRepository;
    private final CommentaireMapper commentaireMapper;

    @Override
    public List<CommentaireResponseDto> findByIdTache(Long idTache) {
        return commentaireRepository.findByIdTache(idTache)
                .stream()
                .map(commentaireMapper::toResponseDto)
                .toList();
    }

    @Override
    public CommentaireResponseDto create(Long idTache, CommentaireRequestDto dto) {
        Commentaire commentaire = commentaireMapper.toEntity(dto, idTache);
        return commentaireMapper.toResponseDto(commentaireRepository.save(commentaire));
    }

    @Override
    public void delete(String id) {
        commentaireRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Commentaire non trouve avec l'id : " + id));
        commentaireRepository.deleteById(id);
    }
}