package fr.ubo.kanban.controllers;

import fr.ubo.kanban.dtos.tache.TacheDto;
import fr.ubo.kanban.services.TacheService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/taches")
@RequiredArgsConstructor
public class TacheController {

    private final TacheService tacheService;

    @GetMapping
    public List<TacheDto> getAllTaches() {
        return tacheService.getAllTaches();
    }

    @GetMapping("/{id}")
    public TacheDto getTacheById(@PathVariable Long id) {
        return tacheService.getTacheById(id);
    }

    @GetMapping("/colonne/{idColonne}")
    public List<TacheDto> getTachesByColonne(@PathVariable Long idColonne) {
        return tacheService.getTachesByColonneId(idColonne);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public TacheDto saveTache(@RequestBody TacheDto dto) {
        return tacheService.saveTache(dto);
    }

    @PutMapping("/{id}")
    public TacheDto updateTache(@PathVariable Long id, @RequestBody TacheDto dto) {
        return tacheService.updateTache(id, dto);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteTache(@PathVariable Long id) {
        tacheService.deleteTache(id);
    }
}