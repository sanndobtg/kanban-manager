package fr.ubo.kanban.controllers;

import fr.ubo.kanban.common.response.ApiResponse;
import fr.ubo.kanban.dtos.commentaire.CommentaireRequestDto;
import fr.ubo.kanban.dtos.commentaire.CommentaireResponseDto;
import fr.ubo.kanban.services.CommentaireService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

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

//    @PostMapping("/api/taches/{idTache}/commentaires")
//    public ResponseEntity<ApiResponse<CommentaireResponseDto>> create(
//            @PathVariable Long idTache,
//            @RequestBody CommentaireRequestDto dto) {
//        return ResponseEntity.ok(ApiResponse.ok(commentaireService.create(idTache, dto)));
//    }

    @PostMapping(value = "/api/taches/{idTache}/commentaires",
            consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<ApiResponse<CommentaireResponseDto>> createWithFiles(
            @PathVariable Long idTache,
            @RequestPart("contenu") String contenu,
            @RequestPart(value = "fichiers", required = false) List<MultipartFile> fichiers) {
        return ResponseEntity.ok(ApiResponse.ok(
                commentaireService.createWithFiles(idTache, new CommentaireRequestDto(contenu), fichiers)));
    }

    @PostMapping(value = "/api/taches/{idTache}/commentaires",
            consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<ApiResponse<CommentaireResponseDto>> create(
            @PathVariable Long idTache,
            @RequestBody CommentaireRequestDto dto) {
        return ResponseEntity.ok(ApiResponse.ok(
                commentaireService.createWithFiles(idTache, dto, null)));
    }

    @DeleteMapping("/api/commentaires/{id}")
    public ResponseEntity<ApiResponse<Void>> delete(@PathVariable String id) {
        commentaireService.delete(id);
        return ResponseEntity.ok(ApiResponse.ok(null));
    }
}