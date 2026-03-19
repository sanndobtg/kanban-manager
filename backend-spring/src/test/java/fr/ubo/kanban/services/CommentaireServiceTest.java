package fr.ubo.kanban.services;

import fr.ubo.kanban.common.exception.NotFoundException;
import fr.ubo.kanban.dtos.commentaire.CommentaireRequestDto;
import fr.ubo.kanban.dtos.commentaire.CommentaireResponseDto;
import fr.ubo.kanban.mappers.CommentaireMapper;
import fr.ubo.kanban.model.Tache;
import fr.ubo.kanban.model.Utilisateur;
import fr.ubo.kanban.model.mongoDB.Commentaire;
import fr.ubo.kanban.repositories.CommentaireRepository;
import fr.ubo.kanban.repositories.TacheRepository;
import fr.ubo.kanban.services.impl.CommentaireServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CommentaireServiceTest {

    @Mock private CommentaireRepository commentaireRepository;
    @Mock private CommentaireMapper commentaireMapper;
    @Mock private TacheRepository tacheRepository;
    @Mock private Authentication authentication;
    @Mock private SecurityContext securityContext;

    @InjectMocks
    private CommentaireServiceImpl commentaireService;

    private Commentaire commentaire;
    private CommentaireResponseDto responseDto;
    private Tache tache;
    private Utilisateur createur;

    @BeforeEach
    void setUp() {
        createur = new Utilisateur();
        createur.setId(1L);

        tache = new Tache();
        tache.setId(1L);
        tache.setCreateur(createur);
        tache.setUtilisateurs(new ArrayList<>(List.of(createur)));

        commentaire = new Commentaire();
        commentaire.setId("abc123");
        commentaire.setIdTache(1L);
        commentaire.setIdUtilisateur(1L);
        commentaire.setContenu("Super tâche !");
        commentaire.setDateCreation(LocalDateTime.now());

        responseDto = new CommentaireResponseDto();
        responseDto.setId("abc123");
        responseDto.setIdTache(1L);
        responseDto.setIdUtilisateur(1L);
        responseDto.setContenu("Super tâche !");

        when(securityContext.getAuthentication()).thenReturn(authentication);
        SecurityContextHolder.setContext(securityContext);
        when(authentication.getName()).thenReturn("1");
    }

    @Test
    void findByIdTache_devraitRetournerCommentaires() {
        when(tacheRepository.findById(1L)).thenReturn(Optional.of(tache));
        when(commentaireRepository.findByIdTache(1L)).thenReturn(List.of(commentaire));
        when(commentaireMapper.toResponseDto(commentaire)).thenReturn(responseDto);

        List<CommentaireResponseDto> result = commentaireService.findByIdTache(1L);

        assertThat(result).hasSize(1);
        assertThat(result.get(0).getContenu()).isEqualTo("Super tâche !");
    }

    @Test
    void findByIdTache_devraitRefuserSiNonAssigne() {
        when(authentication.getName()).thenReturn("99");
        tache.setUtilisateurs(new ArrayList<>());
        when(tacheRepository.findById(1L)).thenReturn(Optional.of(tache));

        assertThatThrownBy(() -> commentaireService.findByIdTache(1L))
                .isInstanceOf(ResponseStatusException.class);
    }

    @Test
    void create_devraitCreerCommentaire() {
        CommentaireRequestDto requestDto = new CommentaireRequestDto();
        requestDto.setContenu("Super tâche !");

        when(tacheRepository.findById(1L)).thenReturn(Optional.of(tache));
        when(commentaireMapper.toEntity(requestDto, 1L, 1L)).thenReturn(commentaire);
        when(commentaireRepository.save(commentaire)).thenReturn(commentaire);
        when(commentaireMapper.toResponseDto(commentaire)).thenReturn(responseDto);

        CommentaireResponseDto result = commentaireService.create(1L, requestDto);

        assertThat(result).isNotNull();
        assertThat(result.getContenu()).isEqualTo("Super tâche !");
    }

    @Test
    void delete_devraitRefuserSiPasAuteur() {
        when(authentication.getName()).thenReturn("99");
        when(commentaireRepository.findById("abc123")).thenReturn(Optional.of(commentaire));

        assertThatThrownBy(() -> commentaireService.delete("abc123"))
                .isInstanceOf(ResponseStatusException.class);

        verify(commentaireRepository, never()).deleteById(any());
    }

    @Test
    void delete_devraitSupprimerSiAuteur() {
        when(commentaireRepository.findById("abc123")).thenReturn(Optional.of(commentaire));

        commentaireService.delete("abc123");

        verify(commentaireRepository, times(1)).deleteById("abc123");
    }
}