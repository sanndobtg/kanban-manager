package fr.ubo.kanban.services;

import fr.ubo.kanban.common.exception.NotFoundException;
import fr.ubo.kanban.dtos.tache.TacheDto;
import fr.ubo.kanban.mappers.TacheMapper;
import fr.ubo.kanban.model.*;
import fr.ubo.kanban.repositories.ColonneRepository;
import fr.ubo.kanban.repositories.TacheRepository;
import fr.ubo.kanban.repositories.UtilisateurRepository;
import fr.ubo.kanban.services.impl.TacheServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.server.ResponseStatusException;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
class TacheServiceTest {

    @Mock private TacheRepository tacheRepository;
    @Mock private ColonneRepository colonneRepository;
    @Mock private UtilisateurRepository utilisateurRepository;
    @Mock private TacheMapper tacheMapper;
    @Mock private Authentication authentication;
    @Mock private SecurityContext securityContext;

    @InjectMocks
    private TacheServiceImpl tacheService;

    private Tache tache;
    private Utilisateur createur;
    private Colonne colonne;
    private TacheDto tacheDto;

    @BeforeEach
    void setUp() {
        createur = new Utilisateur();
        createur.setId(1L);

        colonne = new Colonne();
        colonne.setId(1L);
        colonne.setNom("À faire");

        tache = new Tache();
        tache.setId(1L);
        tache.setTitre("Ma tâche");
        tache.setDescription("Description");
        tache.setColonne(colonne);
        tache.setCreateur(createur);
        tache.setUtilisateurs(new ArrayList<>());
        tache.setPriorite(Priorite.MOYENNE);

        tacheDto = new TacheDto();
        tacheDto.setId(1L);
        tacheDto.setTitre("Ma tâche");
        tacheDto.setIdColonne(1L);
        tacheDto.setIdCreateur(1L);
        tacheDto.setIdUtilisateurs(new ArrayList<>());
        tacheDto.setPriorite(Priorite.MOYENNE);

        when(securityContext.getAuthentication()).thenReturn(authentication);
        SecurityContextHolder.setContext(securityContext);
        when(authentication.getName()).thenReturn("1");
    }

    @Test
    void getAllTaches_devraitRetournerListe() {
        when(tacheRepository.findAll()).thenReturn(List.of(tache));
        when(tacheMapper.toDto(tache)).thenReturn(tacheDto);

        List<TacheDto> result = tacheService.getAllTaches();

        assertThat(result).hasSize(1);
        assertThat(result.get(0).getTitre()).isEqualTo("Ma tâche");
    }

    @Test
    void getTacheById_devraitRetournerTache() {
        when(tacheRepository.findById(1L)).thenReturn(Optional.of(tache));
        when(tacheMapper.toDto(tache)).thenReturn(tacheDto);

        TacheDto result = tacheService.getTacheById(1L);

        assertThat(result).isNotNull();
        assertThat(result.getId()).isEqualTo(1L);
    }

    @Test
    void getTacheById_devraitLancerExceptionSiInexistant() {
        when(tacheRepository.findById(99L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> tacheService.getTacheById(99L))
                .isInstanceOf(NotFoundException.class);
    }

    @Test
    void getTachesByColonneId_devraitRetournerTachesDeLaColonne() {
        when(tacheRepository.findByColonneId(1L)).thenReturn(List.of(tache));
        when(tacheMapper.toDto(tache)).thenReturn(tacheDto);

        List<TacheDto> result = tacheService.getTachesByColonneId(1L);

        assertThat(result).hasSize(1);
        assertThat(result.get(0).getIdColonne()).isEqualTo(1L);
    }

    @Test
    void saveTache_devraitSetCreateurDepuisJwt() {
        when(colonneRepository.findById(1L)).thenReturn(Optional.of(colonne));
        when(utilisateurRepository.findById(1L)).thenReturn(Optional.of(createur));
        when(utilisateurRepository.findAllById(any())).thenReturn(new ArrayList<>());
        when(tacheMapper.toEntity(any(), any(), any(), any())).thenReturn(tache);
        when(tacheRepository.save(tache)).thenReturn(tache);
        when(tacheMapper.toDto(tache)).thenReturn(tacheDto);

        TacheDto result = tacheService.saveTache(tacheDto);

        assertThat(result).isNotNull();
        verify(utilisateurRepository, times(1)).findById(1L);
    }

    @Test
    void deleteTache_devraitSupprimerSiCreateur() {
        when(tacheRepository.findById(1L)).thenReturn(Optional.of(tache));

        tacheService.deleteTache(1L);

        verify(tacheRepository, times(1)).deleteById(1L);
    }

    @Test
    void deleteTache_devraitRefuserSiPasCreateur() {
        when(authentication.getName()).thenReturn("99");
        when(tacheRepository.findById(1L)).thenReturn(Optional.of(tache));

        assertThatThrownBy(() -> tacheService.deleteTache(1L))
                .isInstanceOf(ResponseStatusException.class);

        verify(tacheRepository, never()).deleteById(any());
    }

    @Test
    void ajouterUtilisateur_devraitAjouterSiPasDejaPresent() {
        Utilisateur nouveauMembre = new Utilisateur();
        nouveauMembre.setId(2L);

        when(tacheRepository.findById(1L)).thenReturn(Optional.of(tache));
        when(utilisateurRepository.findById(2L)).thenReturn(Optional.of(nouveauMembre));
        when(tacheRepository.save(tache)).thenReturn(tache);

        tacheService.ajouterUtilisateur(1L, 2L);

        assertThat(tache.getUtilisateurs()).contains(nouveauMembre);
    }

    @Test
    void retirerUtilisateur_devraitRetirerSiPresent() {
        Utilisateur membre = new Utilisateur();
        membre.setId(2L);
        tache.getUtilisateurs().add(membre);

        when(tacheRepository.findById(1L)).thenReturn(Optional.of(tache));
        when(utilisateurRepository.findById(2L)).thenReturn(Optional.of(membre));
        when(tacheRepository.save(tache)).thenReturn(tache);

        tacheService.retirerUtilisateur(1L, 2L);

        assertThat(tache.getUtilisateurs()).doesNotContain(membre);
    }
}