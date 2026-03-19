package fr.ubo.kanban.services;

import fr.ubo.kanban.common.exception.NotFoundException;
import fr.ubo.kanban.dtos.utilisateur.UtilisateurRequestDto;
import fr.ubo.kanban.dtos.utilisateur.UtilisateurResponseDto;
import fr.ubo.kanban.mappers.UtilisateurMapper;
import fr.ubo.kanban.model.Role;
import fr.ubo.kanban.model.Utilisateur;
import fr.ubo.kanban.repositories.UtilisateurRepository;
import fr.ubo.kanban.services.impl.UtilisateurServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
class UtilisateurServiceTest {

    @Mock private UtilisateurRepository utilisateurRepository;
    @Mock private UtilisateurMapper utilisateurMapper;
    @Mock private PasswordEncoder passwordEncoder;

    @InjectMocks
    private UtilisateurServiceImpl utilisateurService;

    private Utilisateur utilisateur;
    private UtilisateurResponseDto responseDto;
    private UtilisateurRequestDto requestDto;

    @BeforeEach
    void setUp() {
        utilisateur = new Utilisateur();
        utilisateur.setId(1L);
        utilisateur.setNom("Dupont");
        utilisateur.setPrenom("Jean");
        utilisateur.setEmail("jean@test.com");
        utilisateur.setMotDePasse("hashedPassword");
        utilisateur.setRole(Role.UTILISATEUR);

        responseDto = new UtilisateurResponseDto();
        responseDto.setId(1L);
        responseDto.setNom("Dupont");
        responseDto.setPrenom("Jean");
        responseDto.setEmail("jean@test.com");
        responseDto.setRole(Role.UTILISATEUR);

        requestDto = new UtilisateurRequestDto();
        requestDto.setNom("Dupont");
        requestDto.setPrenom("Jean");
        requestDto.setEmail("jean@test.com");
        requestDto.setMotDePasse("password123");
        requestDto.setRole(Role.UTILISATEUR);
    }

    @Test
    void getAllUtilisateurs_devraitRetournerListe() {
        when(utilisateurRepository.findAll()).thenReturn(List.of(utilisateur));
        when(utilisateurMapper.toResponseDto(utilisateur)).thenReturn(responseDto);

        List<UtilisateurResponseDto> result = utilisateurService.getAllUtilisateurs();

        assertThat(result).hasSize(1);
        assertThat(result.get(0).getEmail()).isEqualTo("jean@test.com");
        verify(utilisateurRepository, times(1)).findAll();
    }

    @Test
    void getUtilisateurById_devraitRetournerUtilisateur() {
        when(utilisateurRepository.findById(1L)).thenReturn(Optional.of(utilisateur));
        when(utilisateurMapper.toResponseDto(utilisateur)).thenReturn(responseDto);

        UtilisateurResponseDto result = utilisateurService.getUtilisateurById(1L);

        assertThat(result).isNotNull();
        assertThat(result.getId()).isEqualTo(1L);
        assertThat(result.getNom()).isEqualTo("Dupont");
    }



    @Test
    void saveUtilisateur_devraitHasherMotDePasse() {
        when(utilisateurMapper.toEntity(requestDto)).thenReturn(utilisateur);
        when(passwordEncoder.encode("password123")).thenReturn("hashedPassword");
        when(utilisateurRepository.save(utilisateur)).thenReturn(utilisateur);
        when(utilisateurMapper.toResponseDto(utilisateur)).thenReturn(responseDto);

        UtilisateurResponseDto result = utilisateurService.saveUtilisateur(requestDto);

        assertThat(result).isNotNull();
        verify(passwordEncoder, times(1)).encode("password123");
        verify(utilisateurRepository, times(1)).save(utilisateur);
    }

    @Test
    void deleteUtilisateur_devraitSupprimerSiExistant() {
        when(utilisateurRepository.findById(1L)).thenReturn(Optional.of(utilisateur));

        utilisateurService.deleteUtilisateur(1L);

        verify(utilisateurRepository, times(1)).deleteById(1L);
    }



    @Test
    void updateUtilisateur_devraitMettreAJourSansHasherSiPasDeNouveauMotDePasse() {
        requestDto.setMotDePasse(null);
        when(utilisateurRepository.findById(1L)).thenReturn(Optional.of(utilisateur));
        when(utilisateurRepository.save(utilisateur)).thenReturn(utilisateur);
        when(utilisateurMapper.toResponseDto(utilisateur)).thenReturn(responseDto);

        utilisateurService.updateUtilisateur(1L, requestDto);

        verify(passwordEncoder, never()).encode(any());
    }

    @Test
    void updateUtilisateur_devraitHasherSiNouveauMotDePasse() {
        when(utilisateurRepository.findById(1L)).thenReturn(Optional.of(utilisateur));
        when(passwordEncoder.encode("password123")).thenReturn("newHashedPassword");
        when(utilisateurRepository.save(utilisateur)).thenReturn(utilisateur);
        when(utilisateurMapper.toResponseDto(utilisateur)).thenReturn(responseDto);

        utilisateurService.updateUtilisateur(1L, requestDto);

        verify(passwordEncoder, times(1)).encode("password123");
    }
}