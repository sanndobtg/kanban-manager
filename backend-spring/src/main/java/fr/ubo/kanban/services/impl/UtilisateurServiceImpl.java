package fr.ubo.kanban.services.impl;

import fr.ubo.kanban.dtos.utilisateur.UtilisateurRequestDto;
import fr.ubo.kanban.dtos.utilisateur.UtilisateurResponseDto;
import fr.ubo.kanban.mappers.UtilisateurMapper;
import fr.ubo.kanban.model.Utilisateur;
import fr.ubo.kanban.repositories.UtilisateurRepository;
import fr.ubo.kanban.services.UtilisateurService;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class UtilisateurServiceImpl implements UtilisateurService {

    private final UtilisateurRepository utilisateurRepository;
    private final UtilisateurMapper utilisateurMapper;

    @Override
    public UtilisateurResponseDto saveUtilisateur(UtilisateurRequestDto dto) {
        Utilisateur utilisateur = utilisateurRepository.save(utilisateurMapper.toEntity(dto));
        return utilisateurMapper.toResponseDto(utilisateur);
    }

    @Override
    @Transactional(readOnly = true)
    public UtilisateurResponseDto getUtilisateurById(Long id) {
        Utilisateur utilisateur = utilisateurRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException(
                        String.format("L'utilisateur avec l'ID %d n'existe pas", id)));
        return utilisateurMapper.toResponseDto(utilisateur);
    }

    @Override
    public boolean deleteUtilisateur(Long id) {
        utilisateurRepository.deleteById(id);
        return true;
    }

    @Override
    @Transactional(readOnly = true)
    public List<UtilisateurResponseDto> getAllUtilisateurs() {
        return utilisateurRepository.findAll().stream()
                .map(utilisateurMapper::toResponseDto)
                .toList();
    }

    @Override
    public UtilisateurResponseDto updateUtilisateur(Long id, UtilisateurRequestDto dto) {
        Utilisateur utilisateur = utilisateurRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException(
                        String.format("L'utilisateur avec l'ID %d n'existe pas", id)));

        if (dto.getNom() != null) utilisateur.setNom(dto.getNom());
        if (dto.getPrenom() != null) utilisateur.setPrenom(dto.getPrenom());
        if (dto.getEmail() != null) utilisateur.setEmail(dto.getEmail());
        if (dto.getRole() != null) utilisateur.setRole(dto.getRole());
        if (dto.getMotDePasse() != null) utilisateur.setMotDePasse(dto.getMotDePasse());

        return utilisateurMapper.toResponseDto(utilisateurRepository.save(utilisateur));
    }
}
