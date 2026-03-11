package fr.ubo.kanban.services.impl;

import fr.ubo.kanban.dtos.tache.TacheDto;
import fr.ubo.kanban.mappers.TacheMapper;
import fr.ubo.kanban.model.Colonne;
import fr.ubo.kanban.model.Tache;
import fr.ubo.kanban.model.Utilisateur;
import fr.ubo.kanban.repositories.ColonneRepository;
import fr.ubo.kanban.repositories.TacheRepository;
import fr.ubo.kanban.repositories.UtilisateurRepository;
import fr.ubo.kanban.services.TacheService;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class TacheServiceImpl implements TacheService {

    private final TacheRepository tacheRepository;
    private final ColonneRepository colonneRepository;
    private final UtilisateurRepository utilisateurRepository;
    private final TacheMapper tacheMapper;

    @Override
    public TacheDto saveTache(TacheDto dto) {
        Colonne colonne = colonneRepository.findById(dto.getIdColonne())
                .orElseThrow(() -> new EntityNotFoundException(
                        String.format("Colonne %d introuvable", dto.getIdColonne())));
        Utilisateur utilisateur = utilisateurRepository.findById(dto.getIdUtilisateur())
                .orElseThrow(() -> new EntityNotFoundException(
                        String.format("Utilisateur %d introuvable", dto.getIdUtilisateur())));
        Tache tache = tacheRepository.save(tacheMapper.toEntity(dto, colonne, utilisateur));
        return tacheMapper.toDto(tache);
    }

    @Override
    @Transactional(readOnly = true)
    public TacheDto getTacheById(Long id) {
        Tache tache = tacheRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException(
                        String.format("Tache %d introuvable", id)));
        return tacheMapper.toDto(tache);
    }

    @Override
    @Transactional(readOnly = true)
    public List<TacheDto> getAllTaches() {
        return tacheRepository.findAll().stream()
                .map(tacheMapper::toDto)
                .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public List<TacheDto> getTachesByColonneId(Long idColonne) {
        return tacheRepository.findByColonneId(idColonne).stream()
                .map(tacheMapper::toDto)
                .toList();
    }

    @Override
    public TacheDto updateTache(Long id, TacheDto dto) {
        Tache tache = tacheRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException(
                        String.format("Tache %d introuvable", id)));

        if (dto.getTitre() != null) tache.setTitre(dto.getTitre());
        if (dto.getDescription() != null) tache.setDescription(dto.getDescription());
        if (dto.getPriorite() != null) tache.setPriorite(dto.getPriorite());
        if (dto.getIdColonne() != null) {
            Colonne colonne = colonneRepository.findById(dto.getIdColonne())
                    .orElseThrow(() -> new EntityNotFoundException(
                            String.format("Colonne %d introuvable", dto.getIdColonne())));
            tache.setColonne(colonne);
        }
        if (dto.getIdUtilisateur() != null) {
            Utilisateur utilisateur = utilisateurRepository.findById(dto.getIdUtilisateur())
                    .orElseThrow(() -> new EntityNotFoundException(
                            String.format("Utilisateur %d introuvable", dto.getIdUtilisateur())));
            tache.setUtilisateur(utilisateur);
        }

        return tacheMapper.toDto(tacheRepository.save(tache));
    }

    @Override
    public boolean deleteTache(Long id) {
        tacheRepository.deleteById(id);
        return true;
    }
}