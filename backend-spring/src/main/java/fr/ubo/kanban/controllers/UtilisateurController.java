package fr.ubo.kanban.controllers;

import fr.ubo.kanban.dto.UtilisateurDto;
import fr.ubo.kanban.model.Utilisateur;
import fr.ubo.kanban.services.servicesImpl.UtilisateurServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("api/utilisateurs")
public class UtilisateurController {

    @Autowired
    private UtilisateurServiceImpl utilisateurService;

//    public CompteController(CompteServiceImpl compteService) {
//        this.compteService = compteService;
//    }

    @GetMapping
    public List<UtilisateurDto> getUtilisateurs() {
        return utilisateurService.getAllUtilisateurs();
    }

    @GetMapping("/{id}")
    public UtilisateurDto getUtilisateur(@PathVariable Long id){
        return utilisateurService.getUtilisateurById(id);
    }

    @PostMapping
    public UtilisateurDto saveUtilisateur(final @RequestBody UtilisateurDto utilisateurDto){
        return utilisateurService.saveUtilisateur(utilisateurDto);
    }

    @DeleteMapping("/{id}")
    public Boolean deleteUtilisateur(@PathVariable Long id){
        return utilisateurService.deleteUtilisateur(id);
    }

//    @PostMapping("/login")
//    public CompteDto login(@RequestBody CompteDto compteDto) {
//        return compteService.login(compteDto.getPseudo(), compteDto.getMdp());
//    }

    @PutMapping("/{id}")
    public UtilisateurDto updateUtilisateur(@PathVariable Long id, @RequestBody UtilisateurDto utilisateurDto) {
        return utilisateurService.updateUtilisateur(id, utilisateurDto);
    }

}
