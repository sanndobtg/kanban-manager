package fr.ubo.kanban.services;

import fr.ubo.kanban.common.exception.NotFoundException;
import fr.ubo.kanban.dtos.tableau.TableauRequestDto;
import fr.ubo.kanban.dtos.tableau.TableauResponseDto;
import fr.ubo.kanban.mappers.TableauMapper;
import fr.ubo.kanban.model.Tableau;
import fr.ubo.kanban.model.Utilisateur;
import fr.ubo.kanban.repositories.TableauRepository;
import fr.ubo.kanban.repositories.UtilisateurRepository;
import fr.ubo.kanban.services.impl.TableauServiceImpl;
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

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT) // ← règle le problème
class TableauServiceTest {

    @Mock private TableauRepository tableauRepository;
    @Mock private UtilisateurRepository utilisateurRepository;
    @Mock private TableauMapper tableauMapper;
    @Mock private Authentication authentication;
    @Mock private SecurityContext securityContext;

    @InjectMocks
    private TableauServiceImpl tableauService;

    private Tableau tableau;
    private Utilisateur utilisateur;
    private TableauResponseDto responseDto;
    private TableauRequestDto requestDto;

    @BeforeEach
    void setUp() {
        utilisateur = new Utilisateur();
        utilisateur.setId(1L);
        utilisateur.setNom("Dupont");
        utilisateur.setPrenom("Jean");

        tableau = new Tableau();
        tableau.setId(1L);
        tableau.setNom("Mon Tableau");
        tableau.setCreateur(utilisateur);
        tableau.setUtilisateurs(new ArrayList<>(List.of(utilisateur)));

        responseDto = new TableauResponseDto();
        responseDto.setId(1L);
        responseDto.setNom("Mon Tableau");
        responseDto.setIdCreateur(1L);

        requestDto = new TableauRequestDto();
        requestDto.setNom("Mon Tableau");

        // LENIENT car pas utilisé par tous les tests
        when(securityContext.getAuthentication()).thenReturn(authentication);
        SecurityContextHolder.setContext(securityContext);
        when(authentication.getName()).thenReturn("1");
    }



    @Test
    void findById_devraitRetournerTableau() {
        when(tableauRepository.findById(1L)).thenReturn(Optional.of(tableau));
        when(tableauMapper.toResponseDto(tableau)).thenReturn(responseDto);

        TableauResponseDto result = tableauService.findById(1L);

        assertThat(result).isNotNull();
        assertThat(result.getId()).isEqualTo(1L);
    }

    @Test
    void findById_devraitLancerExceptionSiInexistant() {
        when(tableauRepository.findById(99L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> tableauService.findById(99L))
                .isInstanceOf(NotFoundException.class);
    }

    @Test
    void create_devraitAjouterCreateurCommeMembre() {
        when(utilisateurRepository.findById(1L)).thenReturn(Optional.of(utilisateur));
        when(tableauMapper.toEntity(requestDto)).thenReturn(tableau);
        when(tableauRepository.save(tableau)).thenReturn(tableau);
        when(tableauMapper.toResponseDto(tableau)).thenReturn(responseDto);

        TableauResponseDto result = tableauService.create(requestDto);

        assertThat(result).isNotNull();
        assertThat(tableau.getUtilisateurs()).contains(utilisateur);
        assertThat(tableau.getCreateur()).isEqualTo(utilisateur);
    }

    @Test
    void delete_devraitSupprimerSiExistant() {
        when(tableauRepository.findById(1L)).thenReturn(Optional.of(tableau));

        tableauService.delete(1L);

        verify(tableauRepository, times(1)).deleteById(1L);
    }

    @Test
    void delete_devraitLancerExceptionSiInexistant() {
        when(tableauRepository.findById(99L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> tableauService.delete(99L))
                .isInstanceOf(NotFoundException.class);

        verify(tableauRepository, never()).deleteById(any());
    }

    @Test
    void ajouterMembre_devraitAjouterSiPasDejaPresent() {
        Utilisateur nouveauMembre = new Utilisateur();
        nouveauMembre.setId(2L);
        tableau.setUtilisateurs(new ArrayList<>());

        when(tableauRepository.findById(1L)).thenReturn(Optional.of(tableau));
        when(utilisateurRepository.findById(2L)).thenReturn(Optional.of(nouveauMembre));
        when(tableauRepository.save(tableau)).thenReturn(tableau);

        tableauService.ajouterMembre(1L, 2L);

        assertThat(tableau.getUtilisateurs()).contains(nouveauMembre);
    }

    @Test
    void ajouterMembre_neDevraitPasAjouterSiDejaPresent() {
        when(tableauRepository.findById(1L)).thenReturn(Optional.of(tableau));
        when(utilisateurRepository.findById(1L)).thenReturn(Optional.of(utilisateur));

        tableauService.ajouterMembre(1L, 1L);

        verify(tableauRepository, never()).save(any());
    }

    @Test
    void retirerMembre_devraitRefuserSiPasCreateur() {
        when(authentication.getName()).thenReturn("99"); // autre utilisateur
        when(tableauRepository.findById(1L)).thenReturn(Optional.of(tableau));
        when(utilisateurRepository.findById(2L)).thenReturn(Optional.of(utilisateur));

        assertThatThrownBy(() -> tableauService.retirerMembre(1L, 2L))
                .isInstanceOf(Exception.class);

        verify(tableauRepository, never()).save(any());
    }
}