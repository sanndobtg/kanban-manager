package fr.ubo.kanban.mappers;



import fr.ubo.kanban.dto.UtilisateurDto;
import fr.ubo.kanban.model.Utilisateur;
import org.springframework.stereotype.Component;

@Component
public class UtilisateurMapper {


    public UtilisateurDto toDto(Utilisateur utilisateur) {
        if (utilisateur == null) {
            return null;
        }

        UtilisateurDto utilisateurDto = new UtilisateurDto();
        utilisateurDto.setId(utilisateur.getId());
        utilisateurDto.setMotDePasse(utilisateur.getMotDePasse());
        utilisateurDto.setEmail(utilisateur.getEmail());
        utilisateurDto.setNom(utilisateur.getNom());
        utilisateurDto.setPrenom(utilisateur.getPrenom());
        utilisateurDto.setRole(utilisateur.getRole());

        return utilisateurDto;
    }

    public Utilisateur toEntity(UtilisateurDto utilisateurDto) {
        if (utilisateurDto == null) {
            return null;
        }

        Utilisateur utilisateur = new Utilisateur();
        if (utilisateurDto.getId() != null) {
            utilisateur.setId(utilisateurDto.getId());
        }

        utilisateur.setMotDePasse(utilisateurDto.getMotDePasse());
        utilisateur.setEmail(utilisateurDto.getEmail());
        utilisateur.setNom(utilisateurDto.getNom());
        utilisateur.setPrenom(utilisateurDto.getPrenom());
        utilisateur.setRole(utilisateurDto.getRole());

        return utilisateur;
    }

}
