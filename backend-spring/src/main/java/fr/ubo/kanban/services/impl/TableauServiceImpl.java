package fr.ubo.kanban.services.impl;

import fr.ubo.kanban.common.exception.NotFoundException;
import fr.ubo.kanban.dtos.tableau.TableauRequestDto;
import fr.ubo.kanban.dtos.tableau.TableauResponseDto;
import fr.ubo.kanban.mappers.TableauMapper;
import fr.ubo.kanban.model.Tableau;
import fr.ubo.kanban.model.Utilisateur;
import fr.ubo.kanban.repositories.TableauRepository;
import fr.ubo.kanban.repositories.UtilisateurRepository;
import fr.ubo.kanban.services.TableauService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
public class TableauServiceImpl implements TableauService {

    private final TableauRepository tableauRepository;
    private final UtilisateurRepository utilisateurRepository;
    private final TableauMapper tableauMapper;

    @Override
    public List<TableauResponseDto> findAll() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        Long userId = Long.parseLong(authentication.getName());

        // Retourne uniquement les tableaux dont l'utilisateur est membre
        return tableauRepository.findAll()
                .stream()
                .filter(t -> t.getUtilisateurs().stream().anyMatch(u -> u.getId().equals(userId)))
                .map(tableauMapper::toResponseDto)
                .toList();
    }

    @Override
    public TableauResponseDto findById(Long id) {
        Tableau tableau = tableauRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Tableau non trouve avec l'id : " + id));
        return tableauMapper.toResponseDto(tableau);
    }

    @Override
    public TableauResponseDto create(TableauRequestDto dto) {

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String userId = authentication.getName();

        Utilisateur createur = utilisateurRepository.findById(Long.parseLong(userId))
                .orElseThrow(() -> new NotFoundException("Utilisateur non trouvé"));

        Tableau tableau = tableauMapper.toEntity(dto);
        tableau.getUtilisateurs().add(createur);
        return tableauMapper.toResponseDto(tableauRepository.save(tableau));
    }

    @Override
    public TableauResponseDto update(Long id, TableauRequestDto dto) {
        Tableau tableau = tableauRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Tableau non trouve avec l'id : " + id));
        tableau.setNom(dto.getNom());
        return tableauMapper.toResponseDto(tableauRepository.save(tableau));
    }

    @Override
    public void delete(Long id) {
        tableauRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Tableau non trouve avec l'id : " + id));
        tableauRepository.deleteById(id);
    }

    @Override
    public void ajouterMembre(Long idTableau, Long idUtilisateur) {
        Tableau tableau = tableauRepository.findById(idTableau)
                .orElseThrow(() -> new NotFoundException("Tableau non trouve avec l'id : " + idTableau));
        Utilisateur utilisateur = utilisateurRepository.findById(idUtilisateur)
                .orElseThrow(() -> new NotFoundException("Utilisateur non trouve avec l'id : " + idUtilisateur));
        if (!tableau.getUtilisateurs().contains(utilisateur)) {
            tableau.getUtilisateurs().add(utilisateur);
            tableauRepository.save(tableau);
        }
    }

    @Override
    public void retirerMembre(Long idTableau, Long idUtilisateur) {
        Tableau tableau = tableauRepository.findById(idTableau)
                .orElseThrow(() -> new NotFoundException("Tableau non trouve avec l'id : " + idTableau));
        Utilisateur utilisateur = utilisateurRepository.findById(idUtilisateur)
                .orElseThrow(() -> new NotFoundException("Utilisateur non trouve avec l'id : " + idUtilisateur));
        tableau.getUtilisateurs().remove(utilisateur);
        tableauRepository.save(tableau);
    }
}