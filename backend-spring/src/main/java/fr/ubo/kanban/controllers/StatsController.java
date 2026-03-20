package fr.ubo.kanban.controllers;

import fr.ubo.kanban.repositories.TableauRepository;
import fr.ubo.kanban.repositories.TacheRepository;
import fr.ubo.kanban.repositories.UtilisateurRepository;
import fr.ubo.kanban.repositories.CommentaireRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
@RequestMapping("/api/admin/stats")
@RequiredArgsConstructor
public class StatsController {

    private final TableauRepository tableauRepository;
    private final TacheRepository tacheRepository;
    private final UtilisateurRepository utilisateurRepository;
    private final CommentaireRepository commentaireRepository;

    @GetMapping
    public Map<String, Object> getStats() {
        return Map.of(
                "nombreTableaux", tableauRepository.count(),
                "nombreTaches", tacheRepository.count(),
                "nombreUtilisateurs", utilisateurRepository.count(),
                "nombreCommentaires", commentaireRepository.count()
        );
    }
}