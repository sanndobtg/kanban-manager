package fr.ubo.kanban.services;

import fr.ubo.kanban.dto.UtilisateurDto;

import java.util.List;

public interface UtilisateurService {

    UtilisateurDto saveUtilisateur(UtilisateurDto utilisateurDto);

    UtilisateurDto getUtilisateurById(Long UtilisateurId);

    boolean deleteUtilisateur(Long UtilisateurId);

    List<UtilisateurDto> getAllUtilisateurs();

    //UtilisateurDto login(String pseudo, String motDePasse);

    UtilisateurDto updateUtilisateur(Long id, UtilisateurDto utilisateurDto);


}


