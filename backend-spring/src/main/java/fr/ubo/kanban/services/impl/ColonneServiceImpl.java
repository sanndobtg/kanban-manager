package fr.ubo.kanban.services.impl;

import fr.ubo.kanban.dtos.colonne.ColonneDto;
import fr.ubo.kanban.mappers.ColonneMapper;
import fr.ubo.kanban.model.Colonne;
import fr.ubo.kanban.model.Tableau;
import fr.ubo.kanban.repositories.ColonneRepository;
import fr.ubo.kanban.repositories.TableauRepository;
import fr.ubo.kanban.services.ColonneService;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ColonneServiceImpl implements ColonneService {

    private final ColonneRepository colonneRepository;
    private final TableauRepository tableauRepository;
    private final ColonneMapper colonneMapper;

    @Override
    public ColonneDto saveColonne(ColonneDto dto) {
        Tableau tableau = tableauRepository.findById(dto.getIdTableau())
                .orElseThrow(() -> new EntityNotFoundException(
                        String.format("Tableau %d introuvable", dto.getIdTableau())));
        Colonne colonne = colonneRepository.save(colonneMapper.toEntity(dto, tableau));
        return colonneMapper.toDto(colonne);
    }

    @Override
    @Transactional(readOnly = true)
    public ColonneDto getColonneById(Long id) {
        Colonne colonne = colonneRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException(
                        String.format("Colonne %d introuvable", id)));
        return colonneMapper.toDto(colonne);
    }

    @Override
    @Transactional(readOnly = true)
    public List<ColonneDto> getAllColonnes() {
        return colonneRepository.findAll().stream()
                .map(colonneMapper::toDto)
                .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public List<ColonneDto> getColonnesByTableauId(Long idTableau) {
        return colonneRepository.findByTableauId(idTableau).stream()
                .map(colonneMapper::toDto)
                .toList();
    }

    @Override
    public ColonneDto updateColonne(Long id, ColonneDto dto) {
        Colonne colonne = colonneRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException(
                        String.format("Colonne %d introuvable", id)));

        if (dto.getNom() != null) colonne.setNom(dto.getNom());
        if (dto.getIdTableau() != null) {
            Tableau tableau = tableauRepository.findById(dto.getIdTableau())
                    .orElseThrow(() -> new EntityNotFoundException(
                            String.format("Tableau %d introuvable", dto.getIdTableau())));
            colonne.setTableau(tableau);
        }

        return colonneMapper.toDto(colonneRepository.save(colonne));
    }

    @Override
    public boolean deleteColonne(Long id) {
        colonneRepository.deleteById(id);
        return true;
    }
}