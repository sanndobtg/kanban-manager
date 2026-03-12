package fr.ubo.kanban.controllers;

import fr.ubo.kanban.common.response.ApiResponse;
import fr.ubo.kanban.dtos.commentaire.CommentaireRequestDto;
import fr.ubo.kanban.dtos.commentaire.CommentaireResponseDto;
import fr.ubo.kanban.services.CommentaireService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
public class CommentaireController {

    private final CommentaireService commentaireService;

    @GetMapping("/api/taches/{idTache}/commentaires")
    public ResponseEntity<ApiResponse<List<CommentaireResponseDto>>> findByIdTache(
            @PathVariable Long idTache) {
        return ResponseEntity.ok(ApiResponse.ok(commentaireService.findByIdTache(idTache)));
    }

    @PostMapping("/api/taches/{idTache}/commentaires")
    public ResponseEntity<ApiResponse<CommentaireResponseDto>> create(
            @PathVariable Long idTache,
            @RequestBody CommentaireRequestDto dto) {
        return ResponseEntity.ok(ApiResponse.ok(commentaireService.create(idTache, dto)));
    }

    @DeleteMapping("/api/commentaires/{id}")
    public ResponseEntity<ApiResponse<Void>> delete(@PathVariable String id) {
        commentaireService.delete(id);
        return ResponseEntity.ok(ApiResponse.ok(null));
    }
}