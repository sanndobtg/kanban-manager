package fr.ubo.kanban.services.impl;


import fr.ubo.kanban.dtos.utilisateur.UtilisateurDto;
import fr.ubo.kanban.mappers.UtilisateurMapper;
import fr.ubo.kanban.model.Utilisateur;
import fr.ubo.kanban.repositories.UtilisateurRepository;
import fr.ubo.kanban.services.UtilisateurService;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class UtilisateurServiceImpl implements UtilisateurService {

    @Autowired
    public UtilisateurRepository utilisateurRepository;

    @Autowired
    public UtilisateurMapper utilisateurMapper;

    @Override
    public UtilisateurDto saveUtilisateur(UtilisateurDto utilisateurDto) {

        Utilisateur utilisateur = utilisateurRepository.save(utilisateurMapper.toEntity(utilisateurDto));
        return utilisateurMapper.toDto(utilisateur);
    }

    @Override
    @Transactional(readOnly = true)
    public UtilisateurDto getUtilisateurById(Long UtilisateurId) {

        var utilisateur = utilisateurRepository.findById(UtilisateurId)
                .orElseThrow(() -> new EntityNotFoundException(
                        String.format("L'utilisateur avec l'ID %d n'existe pas", UtilisateurId)));

        return utilisateurMapper.toDto(utilisateur);

       // return utilisateurMapper.toDto(utilisateurRepository.findById(UtilisateurId));
    }

    @Override
    public boolean deleteUtilisateur(Long UtilisateurId) {
        utilisateurRepository.deleteById(UtilisateurId);

        return true;
    }

    @Override
    @Transactional(readOnly = true)
    public List<UtilisateurDto> getAllUtilisateurs() {
        return utilisateurRepository.findAll().stream()
                .map(utilisateurMapper::toDto)
                .toList();
    }

    @Override
    public UtilisateurDto updateUtilisateur(Long id, UtilisateurDto utilisateurDto) {

        Utilisateur utilisateur = utilisateurRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException(
                        String.format("Le compte avec l'ID %d n'existe pas", id)));

        // Met à jour uniquement les champs non null
        if (utilisateurDto.getNom() != null) utilisateur.setNom(utilisateurDto.getNom());
        if (utilisateurDto.getPrenom() != null) utilisateur.setPrenom(utilisateurDto.getPrenom());
        if (utilisateurDto.getEmail() != null) utilisateur.setEmail(utilisateurDto.getEmail());
        if (utilisateurDto.getRole() != null) utilisateur.setRole(utilisateurDto.getRole());
        if (utilisateurDto.getMotDePasse() != null) utilisateur.setMotDePasse(utilisateurDto.getMotDePasse());

        return utilisateurMapper.toDto(utilisateurRepository.save(utilisateur));
    }



}
