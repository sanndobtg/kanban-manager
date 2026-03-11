package fr.ubo.kanban.services.impl;

import fr.ubo.kanban.dtos.tableau.TableauDto;
import fr.ubo.kanban.mappers.TableauMapper;
import fr.ubo.kanban.model.Tableau;
import fr.ubo.kanban.repositories.TableauRepository;
import fr.ubo.kanban.services.TableauService;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class TableauServiceImpl implements TableauService {

    private final TableauRepository tableauRepository;
    private final TableauMapper tableauMapper;

    @Override
    public TableauDto saveTableau(TableauDto dto) {
        Tableau tableau = tableauRepository.save(tableauMapper.toEntity(dto));
        return tableauMapper.toDto(tableau);
    }

    @Override
    @Transactional(readOnly = true)
    public TableauDto getTableauById(Long id) {
        Tableau tableau = tableauRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException(
                        String.format("Le tableau avec l'ID %d n'existe pas", id)));
        return tableauMapper.toDto(tableau);
    }

    @Override
    @Transactional(readOnly = true)
    public List<TableauDto> getAllTableaux() {
        return tableauRepository.findAll().stream()
                .map(tableauMapper::toDto)
                .toList();
    }

    @Override
    public TableauDto updateTableau(Long id, TableauDto dto) {
        Tableau tableau = tableauRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException(
                        String.format("Le tableau avec l'ID %d n'existe pas", id)));

        if (dto.getNom() != null) tableau.setNom(dto.getNom());
        if (dto.getUtilisateurs() != null) tableau.setUtilisateurs(dto.getUtilisateurs());

        return tableauMapper.toDto(tableauRepository.save(tableau));
    }

    @Override
    public boolean deleteTableau(Long id) {
        tableauRepository.deleteById(id);
        return true;
    }
}