package fr.ubo.kanban.controllers;

import fr.ubo.kanban.dtos.colonne.ColonneDto;
import fr.ubo.kanban.services.ColonneService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/colonnes")
@RequiredArgsConstructor
public class ColonneController {

    private final ColonneService colonneService;

    @GetMapping
    public List<ColonneDto> getAllColonnes() {
        return colonneService.getAllColonnes();
    }

    @GetMapping("/{id}")
    public ColonneDto getColonneById(@PathVariable Long id) {
        return colonneService.getColonneById(id);
    }

    @GetMapping("/tableau/{idTableau}")
    public List<ColonneDto> getColonnesByTableau(@PathVariable Long idTableau) {
        return colonneService.getColonnesByTableauId(idTableau);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public ColonneDto saveColonne(@RequestBody ColonneDto dto) {
        return colonneService.saveColonne(dto);
    }

    @PutMapping("/{id}")
    public ColonneDto updateColonne(@PathVariable Long id, @RequestBody ColonneDto dto) {
        return colonneService.updateColonne(id, dto);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteColonne(@PathVariable Long id) {
        colonneService.deleteColonne(id);
    }
}