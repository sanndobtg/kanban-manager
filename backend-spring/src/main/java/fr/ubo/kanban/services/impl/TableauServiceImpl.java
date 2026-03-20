package fr.ubo.kanban.services.impl;

import fr.ubo.kanban.common.exception.NotFoundException;
import fr.ubo.kanban.dtos.tableau.TableauRequestDto;
import fr.ubo.kanban.dtos.tableau.TableauResponseDto;
import fr.ubo.kanban.mappers.TableauMapper;
import fr.ubo.kanban.model.Colonne;
import fr.ubo.kanban.model.Tableau;
import fr.ubo.kanban.model.Tache;
import fr.ubo.kanban.model.Utilisateur;
import fr.ubo.kanban.repositories.*;
import fr.ubo.kanban.services.TableauService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
public class TableauServiceImpl implements TableauService {

    private final TableauRepository tableauRepository;
    private final UtilisateurRepository utilisateurRepository;
    private final ColonneRepository colonneRepository;
    private final TacheRepository tacheRepository;
    private final CommentaireRepository commentaireRepository;
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
        Long idCreateur = Long.parseLong(authentication.getName());

        Utilisateur createur = utilisateurRepository.findById(idCreateur)
                .orElseThrow(() -> new NotFoundException("Utilisateur non trouvé"));

        Tableau tableau = tableauMapper.toEntity(dto);
        tableau.setCreateur(createur);
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
        Tableau tableau = tableauRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Tableau non trouve avec l'id : " + id));

        List<Colonne> colonnes = colonneRepository.findByTableauId(id);

        for (Colonne colonne : colonnes) {
            List<Tache> taches = tacheRepository.findByColonneId(colonne.getId());
            for (Tache tache : taches) {
                tache.getUtilisateurs().clear();
                tacheRepository.save(tache);
                commentaireRepository.deleteAllByIdTache(tache.getId());
            }
            tacheRepository.deleteAll(taches);
        }
        colonneRepository.deleteAll(colonnes);

        tableau.getUtilisateurs().clear();
        tableauRepository.save(tableau);

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
                .orElseThrow(() -> new NotFoundException("Tableau non trouvé : " + idTableau));

        // Vérifie que c'est le créateur qui retire
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        Long idConnecte = Long.parseLong(auth.getName());

        if (!tableau.getCreateur().getId().equals(idConnecte)) {
            throw new ResponseStatusException(
                    HttpStatus.FORBIDDEN,
                    "Seul le créateur du tableau peut retirer un membre"
            );
        }

        // Le créateur ne peut pas se retirer lui-même
        if (idUtilisateur.equals(tableau.getCreateur().getId())) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "Le créateur ne peut pas se retirer du tableau"
            );
        }

        Utilisateur utilisateur = utilisateurRepository.findById(idUtilisateur)
                .orElseThrow(() -> new NotFoundException("Utilisateur non trouvé : " + idUtilisateur));
        tableau.getUtilisateurs().remove(utilisateur);
        tableauRepository.save(tableau);
    }
}