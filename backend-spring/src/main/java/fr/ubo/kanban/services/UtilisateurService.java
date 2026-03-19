package fr.ubo.kanban.services;

import fr.ubo.kanban.dtos.utilisateur.UtilisateurRequestDto;
import fr.ubo.kanban.dtos.utilisateur.UtilisateurResponseDto;
import java.util.List;

public interface UtilisateurService {

    UtilisateurResponseDto saveUtilisateur(UtilisateurRequestDto dto);

    UtilisateurResponseDto getUtilisateurById(Long id);

    boolean deleteUtilisateur(Long id);

    List<UtilisateurResponseDto> getAllUtilisateurs();

    UtilisateurResponseDto updateUtilisateur(Long id, UtilisateurRequestDto dto);
}