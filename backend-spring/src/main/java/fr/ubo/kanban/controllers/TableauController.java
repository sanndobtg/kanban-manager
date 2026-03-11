package fr.ubo.kanban.controllers;

import fr.ubo.kanban.dtos.tableau.TableauDto;
import fr.ubo.kanban.services.TableauService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/tableaux")
@RequiredArgsConstructor
public class TableauController {

    private final TableauService tableauService;

    @GetMapping
    public List<TableauDto> getAllTableaux() {
        return tableauService.getAllTableaux();
    }

    @GetMapping("/{id}")
    public TableauDto getTableauById(@PathVariable Long id) {
        return tableauService.getTableauById(id);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public TableauDto saveTableau(@RequestBody TableauDto dto) {
        return tableauService.saveTableau(dto);
    }

    @PutMapping("/{id}")
    public TableauDto updateTableau(@PathVariable Long id, @RequestBody TableauDto dto) {
        return tableauService.updateTableau(id, dto);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteTableau(@PathVariable Long id) {
        tableauService.deleteTableau(id);
    }
}