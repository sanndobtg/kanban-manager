package fr.ubo.kanban.controllers;

import fr.ubo.kanban.dtos.utilisateur.UtilisateurRequestDto;
import fr.ubo.kanban.dtos.utilisateur.UtilisateurResponseDto;
import fr.ubo.kanban.services.UtilisateurService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/utilisateurs")
@RequiredArgsConstructor
public class UtilisateurController {

    private final UtilisateurService utilisateurService;

    @GetMapping
    public List<UtilisateurResponseDto> getUtilisateurs() {
        return utilisateurService.getAllUtilisateurs();
    }

    @GetMapping("/{id}")
    public UtilisateurResponseDto getUtilisateur(@PathVariable Long id) {
        return utilisateurService.getUtilisateurById(id);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public UtilisateurResponseDto saveUtilisateur(@RequestBody UtilisateurRequestDto dto) {
        return utilisateurService.saveUtilisateur(dto);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteUtilisateur(@PathVariable Long id) {
        utilisateurService.deleteUtilisateur(id);
    }

    @PutMapping("/{id}")
    public UtilisateurResponseDto updateUtilisateur(@PathVariable Long id, @RequestBody UtilisateurRequestDto dto) {
        return utilisateurService.updateUtilisateur(id, dto);
    }
}