package fr.ubo.kanban.services.impl;

import fr.ubo.kanban.common.exception.NotFoundException;
import fr.ubo.kanban.dtos.tache.TacheDto;
import fr.ubo.kanban.mappers.TacheMapper;
import fr.ubo.kanban.model.Colonne;
import fr.ubo.kanban.model.Tache;
import fr.ubo.kanban.model.Utilisateur;
import fr.ubo.kanban.repositories.ColonneRepository;
import fr.ubo.kanban.repositories.TacheRepository;
import fr.ubo.kanban.repositories.UtilisateurRepository;
import fr.ubo.kanban.services.TacheService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
public class TacheServiceImpl implements TacheService {

    private final TacheRepository tacheRepository;
    private final ColonneRepository colonneRepository;
    private final UtilisateurRepository utilisateurRepository;
    private final TacheMapper tacheMapper;

    @Override
    public List<TacheDto> getAllTaches() {
        return tacheRepository.findAll().stream()
                .map(tacheMapper::toDto)
                .toList();
    }

    @Override
    public TacheDto getTacheById(Long id) {
        Tache tache = tacheRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Tache non trouvée : " + id));
        return tacheMapper.toDto(tache);
    }

    @Override
    public List<TacheDto> getTachesByColonneId(Long idColonne) {
        return tacheRepository.findByColonneId(idColonne).stream()
                .map(tacheMapper::toDto)
                .toList();
    }

    @Override
    public TacheDto saveTache(TacheDto dto) {
        // Récupère le créateur depuis le JWT
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        Long idCreateur = Long.parseLong(auth.getName());

        Colonne colonne = colonneRepository.findById(dto.getIdColonne())
                .orElseThrow(() -> new NotFoundException("Colonne non trouvée : " + dto.getIdColonne()));

        Utilisateur createur = utilisateurRepository.findById(idCreateur)
                .orElseThrow(() -> new NotFoundException("Utilisateur non trouvé : " + idCreateur));

        // Récupère les utilisateurs assignés si fournis
        List<Utilisateur> utilisateurs = dto.getIdUtilisateurs() != null && !dto.getIdUtilisateurs().isEmpty()
                ? utilisateurRepository.findAllById(dto.getIdUtilisateurs())
                : new ArrayList<>();

        Tache tache = tacheMapper.toEntity(dto, colonne, createur, utilisateurs);
        return tacheMapper.toDto(tacheRepository.save(tache));
    }

    @Override
    public TacheDto updateTache(Long id, TacheDto dto) {
        Tache tache = tacheRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Tache non trouvée : " + id));

        // Met à jour la colonne si elle a changé (drag & drop)
        if (dto.getIdColonne() != null) {
            Colonne colonne = colonneRepository.findById(dto.getIdColonne())
                    .orElseThrow(() -> new NotFoundException("Colonne non trouvée : " + dto.getIdColonne()));
            tache.setColonne(colonne);
        }

        tache.setTitre(dto.getTitre());
        tache.setDescription(dto.getDescription());
        tache.setPriorite(dto.getPriorite());
        tache.setDateLimit(dto.getDateLimit());

        return tacheMapper.toDto(tacheRepository.save(tache));
    }

    @Override
    public void deleteTache(Long id) {
        Tache tache = tacheRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Tache non trouvée : " + id));

        // Vérifie que c'est bien le créateur qui supprime
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        Long idConnecte = Long.parseLong(auth.getName());

        if (!tache.getCreateur().getId().equals(idConnecte)) {
            throw new ResponseStatusException(
                    HttpStatus.FORBIDDEN,
                    "Seul le créateur peut supprimer cette tâche"
            );
        }

        tacheRepository.deleteById(id);
    }

    @Override
    public void ajouterUtilisateur(Long idTache, Long idUtilisateur) {
        Tache tache = tacheRepository.findById(idTache)
                .orElseThrow(() -> new NotFoundException("Tache non trouvée : " + idTache));
        Utilisateur utilisateur = utilisateurRepository.findById(idUtilisateur)
                .orElseThrow(() -> new NotFoundException("Utilisateur non trouvé : " + idUtilisateur));

        if (!tache.getUtilisateurs().contains(utilisateur)) {
            tache.getUtilisateurs().add(utilisateur);
            tacheRepository.save(tache);
        }
    }

    @Override
    public void retirerUtilisateur(Long idTache, Long idUtilisateur) {
        Tache tache = tacheRepository.findById(idTache)
                .orElseThrow(() -> new NotFoundException("Tache non trouvée : " + idTache));
        Utilisateur utilisateur = utilisateurRepository.findById(idUtilisateur)
                .orElseThrow(() -> new NotFoundException("Utilisateur non trouvé : " + idUtilisateur));

        tache.getUtilisateurs().remove(utilisateur);
        tacheRepository.save(tache);
    }
}