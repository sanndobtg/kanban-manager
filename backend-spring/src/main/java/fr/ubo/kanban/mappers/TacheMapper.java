package fr.ubo.kanban.mappers;

import fr.ubo.kanban.dtos.tache.TacheDto;
import fr.ubo.kanban.model.Colonne;
import fr.ubo.kanban.model.Tache;
import fr.ubo.kanban.model.Utilisateur;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

@Component
public class TacheMapper {

    public TacheDto toDto(Tache tache) {
        TacheDto dto = new TacheDto();
        dto.setId(tache.getId());
        dto.setTitre(tache.getTitre());
        dto.setDescription(tache.getDescription());
        dto.setIdColonne(tache.getColonne().getId());

        // Créateur — remplace l'ancien idUtilisateur
        dto.setIdCreateur(tache.getCreateur().getId());

        // Liste des utilisateurs assignés
        List<Long> ids = tache.getUtilisateurs() != null
                ? tache.getUtilisateurs().stream().map(Utilisateur::getId).toList()
                : new ArrayList<>();
        dto.setIdUtilisateurs(ids);

        dto.setPriorite(tache.getPriorite());
        dto.setDateLimit(tache.getDateLimit());
        return dto;
    }

    public Tache toEntity(TacheDto dto, Colonne colonne, Utilisateur createur, List<Utilisateur> utilisateurs) {
        Tache tache = new Tache();
        tache.setTitre(dto.getTitre());
        tache.setDescription(dto.getDescription());
        tache.setColonne(colonne);
        tache.setCreateur(createur);
        tache.setUtilisateurs(utilisateurs != null ? utilisateurs : new ArrayList<>());
        tache.setPriorite(dto.getPriorite());
        tache.setDateLimit(dto.getDateLimit());
        return tache;
    }
}