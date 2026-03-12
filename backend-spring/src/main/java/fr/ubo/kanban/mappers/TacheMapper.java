package fr.ubo.kanban.mappers;

import fr.ubo.kanban.dtos.tache.TacheDto;
import fr.ubo.kanban.model.Colonne;
import fr.ubo.kanban.model.Tache;
import fr.ubo.kanban.model.Utilisateur;
import org.springframework.stereotype.Component;

@Component
public class TacheMapper {

    public TacheDto toDto(Tache tache) {
        TacheDto dto = new TacheDto();
        dto.setId(tache.getId());
        dto.setTitre(tache.getTitre());
        dto.setDescription(tache.getDescription());
        dto.setIdColonne(tache.getColonne().getId());
        dto.setIdUtilisateur(tache.getUtilisateur().getId());
        dto.setPriorite(tache.getPriorite());
        dto.setDateLimit(tache.getDateLimit());
        return dto;
    }

    public Tache toEntity(TacheDto dto, Colonne colonne, Utilisateur utilisateur) {
        Tache tache = new Tache();
        tache.setTitre(dto.getTitre());
        tache.setDescription(dto.getDescription());
        tache.setColonne(colonne);
        tache.setUtilisateur(utilisateur);
        tache.setPriorite(dto.getPriorite());
        tache.setDateLimit(dto.getDateLimit());
        return tache;
    }
}