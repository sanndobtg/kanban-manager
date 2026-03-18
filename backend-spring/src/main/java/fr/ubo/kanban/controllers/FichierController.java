package fr.ubo.kanban.controllers;

import fr.ubo.kanban.common.response.ApiResponse;
import fr.ubo.kanban.dtos.piecejointe.PieceJointeResponseDto;
import fr.ubo.kanban.services.FichierService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("/api/fichiers")
@RequiredArgsConstructor
public class FichierController {

    private final FichierService fichierService;

    @PostMapping("/upload")
    public ResponseEntity<ApiResponse<PieceJointeResponseDto>> upload(
            @RequestParam("file") MultipartFile file,
            Authentication authentication) {
        Long idUtilisateur = Long.parseLong(authentication.getName());
        return ResponseEntity.ok(ApiResponse.ok(fichierService.upload(file, idUtilisateur)));
    }

    @GetMapping("/{id}/download")
    public ResponseEntity<byte[]> download(@PathVariable String id) {
        return ResponseEntity.ok()
                .contentType(MediaType.parseMediaType(fichierService.getContentType(id)))
                .header(HttpHeaders.CONTENT_DISPOSITION,
                        "attachment; filename=\"" + fichierService.getNomFichier(id) + "\"")
                .body(fichierService.getContenu(id));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<Void>> delete(@PathVariable String id) {
        fichierService.delete(id);
        return ResponseEntity.ok(ApiResponse.ok(null));
    }
}