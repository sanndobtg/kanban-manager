package fr.ubo.kanban.services.impl;

import fr.ubo.kanban.common.exception.NotFoundException;
import fr.ubo.kanban.dtos.utilisateur.UtilisateurRequestDto;
import fr.ubo.kanban.dtos.utilisateur.UtilisateurResponseDto;
import fr.ubo.kanban.mappers.UtilisateurMapper;
import fr.ubo.kanban.model.Utilisateur;
import fr.ubo.kanban.repositories.UtilisateurRepository;
import fr.ubo.kanban.services.UtilisateurService;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class UtilisateurServiceImpl implements UtilisateurService {

    private final UtilisateurRepository utilisateurRepository;
    private final UtilisateurMapper utilisateurMapper;

    private final PasswordEncoder passwordEncoder;



    @Override
    public UtilisateurResponseDto saveUtilisateur(UtilisateurRequestDto dto) {
        Utilisateur utilisateur = utilisateurMapper.toEntity(dto);
        // Hashe le mot de passe avant de sauvegarder
        utilisateur.setMotDePasse(passwordEncoder.encode(dto.getMotDePasse()));
        return utilisateurMapper.toResponseDto(utilisateurRepository.save(utilisateur));
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
                .orElseThrow(() -> new NotFoundException("Utilisateur non trouvé : " + id));
        utilisateur.setNom(dto.getNom());
        utilisateur.setPrenom(dto.getPrenom());
        utilisateur.setEmail(dto.getEmail());
        utilisateur.setRole(dto.getRole());
        // Hashe seulement si un nouveau mot de passe est fourni
        if (dto.getMotDePasse() != null && !dto.getMotDePasse().isBlank()) {
            utilisateur.setMotDePasse(passwordEncoder.encode(dto.getMotDePasse()));
        }
        return utilisateurMapper.toResponseDto(utilisateurRepository.save(utilisateur));
    }
}
