package fr.ubo.kanban.mappers;

import fr.ubo.kanban.dtos.utilisateur.UtilisateurRequestDto;
import fr.ubo.kanban.dtos.utilisateur.UtilisateurResponseDto;
import fr.ubo.kanban.model.Utilisateur;
import org.springframework.stereotype.Component;

@Component
public class UtilisateurMapper {

    // Entité → ResponseDto (ce qu'on renvoie au client)
    public UtilisateurResponseDto toResponseDto(Utilisateur utilisateur) {
        if (utilisateur == null) return null;

        UtilisateurResponseDto dto = new UtilisateurResponseDto();
        dto.setId(utilisateur.getId());
        dto.setNom(utilisateur.getNom());
        dto.setPrenom(utilisateur.getPrenom());
        dto.setEmail(utilisateur.getEmail());
        dto.setRole(utilisateur.getRole());
        // pas de motDePasse !

        return dto;
    }

    // RequestDto → Entité (ce qu'on reçoit du client)
    public Utilisateur toEntity(UtilisateurRequestDto dto) {
        if (dto == null) return null;

        Utilisateur utilisateur = new Utilisateur();
        utilisateur.setNom(dto.getNom());
        utilisateur.setPrenom(dto.getPrenom());
        utilisateur.setEmail(dto.getEmail());
        utilisateur.setMotDePasse(dto.getMotDePasse());
        utilisateur.setRole(dto.getRole());
        // pas d'id car c'est une création

        return utilisateur;
    }
}