package fr.ubo.kanban.services;

import fr.ubo.kanban.dtos.colonne.ColonneDto;
import fr.ubo.kanban.mappers.ColonneMapper;
import fr.ubo.kanban.model.Colonne;
import fr.ubo.kanban.model.Tableau;
import fr.ubo.kanban.repositories.ColonneRepository;
import fr.ubo.kanban.repositories.TableauRepository;
import fr.ubo.kanban.services.impl.ColonneServiceImpl;
import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ColonneServiceTest {

    @Mock private ColonneRepository colonneRepository;
    @Mock private TableauRepository tableauRepository;
    @Mock private ColonneMapper colonneMapper;

    @InjectMocks
    private ColonneServiceImpl colonneService;

    private Colonne colonne;
    private Tableau tableau;
    private ColonneDto colonneDto;

    @BeforeEach
    void setUp() {
        tableau = new Tableau();
        tableau.setId(1L);
        tableau.setNom("Mon Tableau");

        colonne = new Colonne();
        colonne.setId(1L);
        colonne.setNom("À faire");
        colonne.setTableau(tableau);

        colonneDto = new ColonneDto();
        colonneDto.setId(1L);
        colonneDto.setNom("À faire");
        colonneDto.setIdTableau(1L);
    }

    @Test
    void getAllColonnes_devraitRetournerListe() {
        when(colonneRepository.findAll()).thenReturn(List.of(colonne));
        when(colonneMapper.toDto(colonne)).thenReturn(colonneDto);

        List<ColonneDto> result = colonneService.getAllColonnes();

        assertThat(result).hasSize(1);
        assertThat(result.get(0).getNom()).isEqualTo("À faire");
        verify(colonneRepository, times(1)).findAll();
    }

    @Test
    void getColonneById_devraitRetournerColonne() {
        when(colonneRepository.findById(1L)).thenReturn(Optional.of(colonne));
        when(colonneMapper.toDto(colonne)).thenReturn(colonneDto);

        ColonneDto result = colonneService.getColonneById(1L);

        assertThat(result).isNotNull();
        assertThat(result.getId()).isEqualTo(1L);
        assertThat(result.getNom()).isEqualTo("À faire");
    }

    @Test
    void getColonneById_devraitLancerEntityNotFoundExceptionSiInexistant() {
        when(colonneRepository.findById(99L)).thenReturn(Optional.empty());

        // Ton service lance EntityNotFoundException (pas NotFoundException)
        assertThatThrownBy(() -> colonneService.getColonneById(99L))
                .isInstanceOf(EntityNotFoundException.class)
                .hasMessageContaining("99");
    }

    @Test
    void getColonnesByTableauId_devraitRetournerColonnesDuTableau() {
        when(colonneRepository.findByTableauId(1L)).thenReturn(List.of(colonne));
        when(colonneMapper.toDto(colonne)).thenReturn(colonneDto);

        List<ColonneDto> result = colonneService.getColonnesByTableauId(1L);

        assertThat(result).hasSize(1);
        assertThat(result.get(0).getIdTableau()).isEqualTo(1L);
    }

    @Test
    void getColonnesByTableauId_devraitRetournerListeVideSiAucuneColonne() {
        when(colonneRepository.findByTableauId(99L)).thenReturn(List.of());

        List<ColonneDto> result = colonneService.getColonnesByTableauId(99L);

        assertThat(result).isEmpty();
    }

    @Test
    void saveColonne_devraitSauvegarderEtRetournerDto() {
        when(tableauRepository.findById(1L)).thenReturn(Optional.of(tableau));
        when(colonneMapper.toEntity(colonneDto, tableau)).thenReturn(colonne);
        when(colonneRepository.save(colonne)).thenReturn(colonne);
        when(colonneMapper.toDto(colonne)).thenReturn(colonneDto);

        ColonneDto result = colonneService.saveColonne(colonneDto);

        assertThat(result).isNotNull();
        assertThat(result.getNom()).isEqualTo("À faire");
        verify(colonneRepository, times(1)).save(colonne);
    }

    @Test
    void saveColonne_devraitLancerExceptionSiTableauInexistant() {
        when(tableauRepository.findById(1L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> colonneService.saveColonne(colonneDto))
                .isInstanceOf(EntityNotFoundException.class)
                .hasMessageContaining("1");

        verify(colonneRepository, never()).save(any());
    }

    @Test
    void updateColonne_devraitMettreAJourNom() {
        ColonneDto updateDto = new ColonneDto();
        updateDto.setNom("En cours");
        updateDto.setIdTableau(null);

        when(colonneRepository.findById(1L)).thenReturn(Optional.of(colonne));
        when(colonneRepository.save(colonne)).thenReturn(colonne);
        when(colonneMapper.toDto(colonne)).thenReturn(colonneDto);

        colonneService.updateColonne(1L, updateDto);

        assertThat(colonne.getNom()).isEqualTo("En cours");
        verify(colonneRepository, times(1)).save(colonne);
    }

    @Test
    void updateColonne_devraitLancerExceptionSiInexistant() {
        when(colonneRepository.findById(99L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> colonneService.updateColonne(99L, colonneDto))
                .isInstanceOf(EntityNotFoundException.class);

        verify(colonneRepository, never()).save(any());
    }

    @Test
    void deleteColonne_devraitSupprimerEtRetournerTrue() {
        // Ton service ne vérifie pas l'existence avant de supprimer
        // Il appelle directement deleteById
        doNothing().when(colonneRepository).deleteById(1L);

        boolean result = colonneService.deleteColonne(1L);

        assertThat(result).isTrue();
        verify(colonneRepository, times(1)).deleteById(1L);
    }
}

