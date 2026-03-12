package fr.ubo.kanban.controllers;

import fr.ubo.kanban.common.response.ApiResponse;
import fr.ubo.kanban.dtos.tableau.TableauRequestDto;
import fr.ubo.kanban.dtos.tableau.TableauResponseDto;
import fr.ubo.kanban.services.TableauService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/tableaux")
@RequiredArgsConstructor
public class TableauController {

    private final TableauService tableauService;

    @GetMapping
    public ResponseEntity<ApiResponse<List<TableauResponseDto>>> findAll() {
        return ResponseEntity.ok(ApiResponse.ok(tableauService.findAll()));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<TableauResponseDto>> findById(@PathVariable Long id) {
        return ResponseEntity.ok(ApiResponse.ok(tableauService.findById(id)));
    }

    @PostMapping
    public ResponseEntity<ApiResponse<TableauResponseDto>> create(@RequestBody TableauRequestDto dto) {
        return ResponseEntity.ok(ApiResponse.ok(tableauService.create(dto)));
    }

    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse<TableauResponseDto>> update(
            @PathVariable Long id,
            @RequestBody TableauRequestDto dto) {
        return ResponseEntity.ok(ApiResponse.ok(tableauService.update(id, dto)));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<Void>> delete(@PathVariable Long id) {
        tableauService.delete(id);
        return ResponseEntity.ok(ApiResponse.ok(null));
    }

    @PostMapping("/{idTableau}/membres/{idUtilisateur}")
    public ResponseEntity<ApiResponse<Void>> ajouterMembre(
            @PathVariable Long idTableau,
            @PathVariable Long idUtilisateur) {
        tableauService.ajouterMembre(idTableau, idUtilisateur);
        return ResponseEntity.ok(ApiResponse.ok(null));
    }

    @DeleteMapping("/{idTableau}/membres/{idUtilisateur}")
    public ResponseEntity<ApiResponse<Void>> retirerMembre(
            @PathVariable Long idTableau,
            @PathVariable Long idUtilisateur) {
        tableauService.retirerMembre(idTableau, idUtilisateur);
        return ResponseEntity.ok(ApiResponse.ok(null));
    }
}